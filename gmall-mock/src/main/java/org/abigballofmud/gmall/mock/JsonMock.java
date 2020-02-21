package org.abigballofmud.gmall.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.gmall.mock.utils.RandomDateUtil;
import org.abigballofmud.gmall.mock.utils.RandomNumber;
import org.abigballofmud.gmall.mock.utils.RandomOpt;
import org.abigballofmud.gmall.mock.utils.RandomOptGroup;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/11 23:38
 * @since 1.0
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class JsonMock {

    private int startupNum = 10000;
    private int eventNum = 20000;
    private String appId = "gmall2020";
    private ObjectMapper objectMapper = new ObjectMapper();
    private Random random = new Random();

    RandomDateUtil randomDateUtil = null;

    RandomOpt[] osOpts = {
            new RandomOpt("ios", 3),
            new RandomOpt("android", 7)
    };
    RandomOptGroup<String> osOptionGroup = new RandomOptGroup<>(osOpts);

    RandomOpt[] areaOpts = {
            new RandomOpt("beijing", 10),
            new RandomOpt("shanghai", 10),
            new RandomOpt("guangdong", 20),
            new RandomOpt("hebei", 5),
            new RandomOpt("heilongjiang", 5),
            new RandomOpt("shangdong", 5),
            new RandomOpt("tianjin", 5),
            new RandomOpt("shanxi", 5),
            new RandomOpt("sichuan", 5),
            new RandomOpt("chengdu", 5),
            new RandomOpt("hangzhou", 5)
    };
    RandomOptGroup<String> areaOptionGroup = new RandomOptGroup<>(areaOpts);

    RandomOpt[] vsOpts = {
            new RandomOpt("1.2.0", 50),
            new RandomOpt("1.1.2", 15),
            new RandomOpt("1.1.3", 30),
            new RandomOpt("1.1.1", 5)
    };
    RandomOptGroup<String> vsOptionGroup = new RandomOptGroup<>(vsOpts);

    RandomOpt[] eventOpts = {
            new RandomOpt("addFavor", 10),
            new RandomOpt("addComment", 30),
            new RandomOpt("addCart", 20),
            new RandomOpt("clickItem", 40)
    };
    RandomOptGroup<String> eventOptionGroup = new RandomOptGroup<>(eventOpts);

    RandomOpt[] channelOpts = {
            new RandomOpt("xiaomi", 10),
            new RandomOpt("huawei", 20),
            new RandomOpt("wandoujia", 30),
            new RandomOpt("360", 20),
            new RandomOpt("tencent", 20),
            new RandomOpt("baidu", 10),
            new RandomOpt("website", 10)
    };
    RandomOptGroup<String> channelOptionGroup = new RandomOptGroup<>(channelOpts);

    RandomOpt[] quitOpts = {
            new RandomOpt(true, 20),
            new RandomOpt(false, 80)
    };
    RandomOptGroup<Boolean> isQuitOptionGroup = new RandomOptGroup<>(quitOpts);

    private String initStartupLog() {
        /*
        type     string     日志类型
        mid      string     设备唯一标识
        uid      string     用户标识
        os       string     操作系统
        appId    string     应用id
        vs       string     版本号
        ts       bigint     启动时间
        area     string     城市
         */
        String mid = "mid_" + RandomNumber.getRandomInt(1, 1000);
        String uid = "uid_" + RandomNumber.getRandomInt(1, 1000);
        String os = osOptionGroup.getRandomOpt().getValue();
        String area = areaOptionGroup.getRandomOpt().getValue();
        String vs = vsOptionGroup.getRandomOpt().getValue();
        String ch = "ios".equals(os) ? "appstore" : channelOptionGroup.getRandomOpt().getValue();

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("type", "startup");
        map.put("mid", mid);
        map.put("uid", uid);
        map.put("os", os);
        map.put("appId", this.appId);
        map.put("area", area);
        map.put("ch", ch);
        map.put("vs", vs);
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String initEventLog(String startupLog) {
        /*
        type     string     日志类型
        mid      string     设备唯一标识
        uid      string     用户标识
        os       string     操作系统
        appId    string     应用id
        area     string     城市
        evid     string     事件id
        pgid     string     当前页
        npgid    string     跳转页
        itemid   string     商品编号
        ts       bigint     启动时间
         */
        try {
            @SuppressWarnings("rawtypes")
            Map startLogMap = objectMapper.readValue(startupLog, Map.class);
            String mid = String.valueOf(startLogMap.get("mid"));
            String uid = String.valueOf(startLogMap.get("uid"));
            String os = String.valueOf(startLogMap.get("os"));
            String area = String.valueOf(startLogMap.get("area"));
            String evid = eventOptionGroup.getRandomOpt().getValue();
            int pgid = random.nextInt(50) + 1;
            int npgid = random.nextInt(50) + 1;
            int itemid = random.nextInt(50);

            HashMap<String, Object> map = new HashMap<>(16);
            map.put("type", "event");
            map.put("mid", mid);
            map.put("uid", uid);
            map.put("os", os);
            map.put("appId", this.appId);
            map.put("area", area);
            map.put("pgid", pgid);
            map.put("evid", evid);
            map.put("npgid", npgid);
            map.put("itemid", itemid);
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SneakyThrows
    public static void genLog() {
        JsonMock jsonMock = new JsonMock();
        jsonMock.startupNum = 100000;
        for (int i = 0; i < jsonMock.startupNum; i++) {
            String startupLog = jsonMock.initStartupLog();
            jsonMock.sendLog(startupLog);
            while (jsonMock.isQuitOptionGroup.getRandomOpt().getValue()) {
                String eventLog = jsonMock.initEventLog(startupLog);
                jsonMock.sendLog(eventLog);
            }
            // 睡一下 防止发送太快，处理不了
            TimeUnit.MILLISECONDS.sleep(20L);
        }
    }

    public void sendLog(String logContent) {
        LogUploader.sendLog(logContent);
    }

    public static void main(String[] args) {
        genLog();
    }
}
