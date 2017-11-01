
function PublicTool(){};

/**
 * 全局变量工具类
 */
window['tool'] = window['tool'] || new PublicTool();

/**
 * 创建一个进度条
 */
PublicTool.prototype.CreateProgressBar=function(){
		$("<div class=\"div-mymask\"></div>").css({
			display:"block",
			width:"100%",
			height:$(window).height()
			}).appendTo("body");
		$("<div class=\"div-mymask-msg\"></div>").html("正在处理，请稍候……").appendTo("body").css({"font-size": "12px",display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2}); 
};

/**
 * 关闭一个进度条
 */
PublicTool.prototype.CloseProgressBar=function(){
	$(".div-mymask").remove();
	$(".div-mymask-msg").remove();
};
/**
 * @author 张路 2017年8月27日10:04:51
 * 对JavaScript原生的Array集合做了一下拓展
 * 往一个Array集合插入一个指定下标的内容
 * Index为目标下标,item为内容
 */
Array.prototype.insert = function(index, item) {
	this.splice(index, 0, item);
};