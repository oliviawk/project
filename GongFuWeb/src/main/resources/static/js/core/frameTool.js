window["frameTool"] = new FrameTool();
function FrameTool() {
	this.iframeId = "page-wrapper-iframe";// frame 框架id号

};
function getNowFormatDate() {
	var date = new Date();
	var seperator1 = "-";
	var seperator2 = ":";
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = date.getFullYear() + seperator1 + month + seperator1
			+ strDate + " " + date.getHours() + seperator2 + date.getMinutes()
			+ seperator2 + date.getSeconds();
	return currentdate;
}
/**
 * 
 */
$.extend(FrameTool.prototype, {
	/**
	 * 加载主内容
	 * 
	 * @param url
	 * @returns
	 */
	loadMainFrame : function(url) {
		if (url == null) {
			$("#page-wrapper").html("");
		}
		if(url.indexOf("?") > 0){
            url = url + "&random=" + Math.random();
		}else{
            url = url + "?random=" + Math.random();
        }
		$("#page-wrapper").load(url, {
			"random" : Math.random()
			}, function() {
		});
	},
	
	
	selectOneMenu:function(){
		//默认选中第一个菜单
		var li_first = $("#nav-accordion").find("li>a:first");
		$(li_first).click();
		
	},
	initClick:function(){
		$(".sidebar-menu").on("click", function(t) {
			var i = $(t.target).closest("a"),
				u, r, f;
			if (i && i.length != 0) {
				var href = i.attr("href");
				$("#external-frame").attr("src",href);
				return false;
			}
		});
	}
});
