function PublicTool() {
};

/**
 * 全局变量工具类
 */
window['tool'] = window['tool'] || new PublicTool();

/**
 * 创建一个进度条
 */
PublicTool.prototype.CreateProgressBar = function () {
    $("<div class=\"div-mymask\"></div>").css({
        display: "block",
        width: "100%",
        height: $(window).height()
    }).appendTo("body");
    $("<div class=\"div-mymask-msg\"></div>").html("正在处理，请稍候……").appendTo(
        "body").css({
        "font-size": "12px",
        display: "block",
        left: ($(document.body).outerWidth(true) - 190) / 2,
        top: ($(window).height() - 45) / 2
    });
};

/**
 * 关闭一个进度条
 */
PublicTool.prototype.CloseProgressBar = function () {
    $(".div-mymask").remove();
    $(".div-mymask-msg").remove();
};
/**
 * @author 张路 2017年8月27日10:04:51 对JavaScript原生的Array集合做了一下拓展
 *         往一个Array集合插入一个指定下标的内容 Index为目标下标,item为内容
 */
/*
 * Array.prototype.insert = function(index, item) { this.splice(index, 0, item); };
 */
/**
 * 将一个Unix时间戳转换成一个JavaScript时间对象
 *
 * @author ZhangLu 2017年9月13日15:19:55
 */
PublicTool.prototype.getDateByUnixTime = function (unixTime) {
    var date = new Date();
    date.setTime(unixTime);
    return date;
}


PublicTool.prototype.getUTCDateByUnixTime = function (unixTime) {
    var date = new Date();
    date.setTime(unixTime);
    var localTime = date.getTime();
    var localOffset = date.getTimezoneOffset() * 60000; //获得当地时间偏移的毫秒数
    var utcTime = localTime + localOffset; //utc即GMT时间
    date.setTime(utcTime);
    return date;
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { // author: meizz
    var o = {
        "M+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "H+": this.getHours(),
        "h+": this.getHours(), // 小时
        "m+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        "S": this.getMilliseconds()
        // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
            .substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
                : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

/**
 * 按照UTC时间进行解析,用法同上
 */
Date.prototype.FormatUTC = function (fmt) { // author: meizz
    var localTime = this.getTime();
    var localOffset = this.getTimezoneOffset() * 60000; //获得当地时间偏移的毫秒数
    var utcTime = localTime + localOffset; //utc即GMT时间
    var newDate = new Date(utcTime);


    var o = {
        "M+": newDate.getMonth() + 1, // 月份
        "d+": newDate.getDate(), // 日
        "H+": newDate.getHours(),
        "h+": newDate.getHours(), // 小时
        "m+": newDate.getMinutes(), // 分
        "s+": newDate.getSeconds(), // 秒
        "q+": Math.floor((newDate.getMonth() + 3) / 3), // 季度
        "S": newDate.getMilliseconds()
        // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (newDate.getFullYear() + "")
            .substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
                : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

PublicTool.prototype.test = function () {
    alert("test");
}
