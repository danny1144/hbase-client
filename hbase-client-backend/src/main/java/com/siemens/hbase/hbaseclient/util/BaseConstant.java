package com.siemens.hbase.hbaseclient.util;

/**
 * @author zxp
 * @date 2019/11/01
 * @description 基础参数常量
 */
public final class BaseConstant {

    private BaseConstant() {
    }

    /**
     * 批量处理容量阈值
     */
    public static final Integer BATCH_THRESHOLD = 500;

    /**
     * 初始0值
     */
    public static final Long INITIAL_ZERO = 0L;


    /**
     * 初始0值
     */
    public static final Integer INITIAL_ZERO_INT = 0;

    /**
     * 启用
     */
    public static final Integer START = 1;

    /**
     * 值2
     */
    public static final Integer ONE = 1;

    /**
     * 值2
     */
    public static final Integer TWO = 2;

    /**
     * 值3
     */
    public static final Integer THREE = 3;

    /**
     * 值4
     */
    public static final Integer FOUR = 4;
    /**
     * 值5
     */
    public static final Integer FIVE = 5;
    /**
     * 值7
     */
    public static final Integer SEVEN = 7;

    public static final Integer EIGHT = 8;
    /**
     * 值10
     */
    public static final Integer TEN = 10;
    /**
     * 值20
     */
    public static final Integer TWENTY = 20;
    /**
     * 值30
     */
    public static final Integer THIRTY = 30;
    /**
     * 值40
     */
    public static final Integer FORTY = 40;
    /**
     * 值45
     */
    public static final Integer FORTY_FIVE = 45;
    /**
     * 值50
     */
    public static final Integer FIFTY = 50;
    /**
     * 值60
     */
    public static final Integer SIXTY = 60;
    /**
     * 浮点型值10
     */
    public static final Float FLOAT_TEN = 10.0F;
    /**
     * 停用
     */
    public static final Integer STOP = 0;

    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";

    /**
     * 点字符串
     */
    public static final String DOT_STRING = ".";

    /**
     * 逗号字符串
     */
    public static final String COMMA_STRING = ",";

    /**
     * 短横杠字符串
     */
    public static final String SHORT_DASH_STRING = "-";

    /**
     * 下划线字符串
     */
    public static final String UNDERLINE_STRING = "_";

    /**
     * 斜杠字符串
     */
    public static final String SLASH_STRING = "/";

    /**
     * 换行字符
     */
    public static final char LINE_FEED_CHAR = '\n';


    /**
     * Object的null值
     */
    public static final Object OBJECT_NULL = null;

    /**
     * null字符串值
     */
    public static final String EMPTY_NULL_STRING = "null";

    /**
     * 请求头信息
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * utf-8字符集
     */
    public static final String UTF8_CHARSET = "UTF-8";

    /**
     * 24小时
     */
    public static final Integer TWENTY_FOUR_HOUR = 24;

    /**
     * 点分割两字符
     */
    public static final String TWO_CHARACTER_DOT = "%s.%s";

    /**
     * 点分割三字符
     */
    public static final String THREE_CHARACTER_DOT = "%s.%s.%s";

    /**
     * 下划线分割两字符
     */
    public static final String UNDERLINE_CHARACTER = "%s_%s";

    /**
     * 逗号分割三字符
     */
    public static final String THREE_CHARACTER_COMMA = "%s,%s,%s";

    /**
     * 逗号分割两字符
     */
    public static final String TWO_CHARACTER_COMMA = "%s,%s";

    /**
     * All
     */
    public static final String ALL = "ALL";


    /**
     * All
     */
    public static final Long ALLValue = 0L;

    public static final String SENSOR_GROUP = "SENSORGROUP";


    public static final String SENSOR = "SENSOR";

    public static final String N_A = "N.A.";

    public static final String HORIZONTAL_BAR = "- -";

    /**
     * 1毫秒数
     */
    public static final Integer ONE_SECOND_MILLISECOND = 1000;
    /**
     * 1分钟毫秒数
     */
    public static final Long ONE_MINUTE_MILLISECOND = 60000L;
    /**
     * 10分钟毫秒数
     */
    public static final Long TEN_MINUTE_MILLISECOND = 600000L;
    /**
     * 1小时毫秒数
     */
    public static final Long ONE_HOUR_MILLISECOND = 3600000L;
    /**
     * 1天毫秒数
     */
    public static final Long ONE_DAY_MILLISECOND = 86400000L;
    /**
     * 60秒
     */
    public static final Integer SIXTY_SECONDS = 60;
    /**
     * 100
     */
    public static final Integer ONE_HUNDRED = 100;

    /**
     * 模式主键前缀
     */
    public static final String predix = "PMA";
    /**
     * 机组负荷
     */
    public static final String UNIT_LOAD = "unitLoad";

    /**
     * 滤网压损id
     */
    public static final String FILTER_PRESSURE_LOSS = "filterPressure";


    public static final String OFF_LINE_CALCULATE_REDIS_KEY = "zc-pma:offLineCalculateRedisKey";


    public static final String OPERATING_SERIES = "OPERATING_SERIES";


    public static final String SEARCH_BEST_OPERATION = "SEARCH_BEST_OPERATION";


}
