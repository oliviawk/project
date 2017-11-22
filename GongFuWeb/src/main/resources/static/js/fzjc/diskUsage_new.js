var tau = 2 * Math.PI; // http://tauday.com/tau-manifesto

var intervalID_directorUsage;
function directorUsage(url, id,params) {
	
	function generate(data, id) {
		var popoverObj;
//		for (var i = 0; i < 0; i++) {   测试用
//			data.values.shift();
//		}
		var width = 383;
		var height = 200 - 5;
		
		if( data.values.length > 5 ){
			height = height + (data.values.length - 5) * 48;		//当磁盘超过6个时，通过增加高度的方式产生滚动条
		}
		
		var rectBgheight; 											//进度条高度
		if( height / (data.values.length * 2.2) > 30 ){
			rectBgheight = 30;
		}else if( height / (data.values.length * 2.2) < 24 ){
			rectBgheight = 21.8;
		}else{
			rectBgheight = height / (data.values.length * 2.3);	
		}
		var rectBgspace = rectBgheight * 2.2;						//进度条间隔
		
		var marginTop=0;
//		if(data.values.length%2==0){
//			marginTop=(height-(80*(data.values.length/2)))/2;
//		}else{
//			marginTop=(height-(80*(Math.floor(data.values.length/2)+1)))/2;
//		}
		var length = data.values.length;
//		marginTop = (height - (length * rectBgheight + (length - 1) * rectBgspace))/2;
		marginTop = (height - length * rectBgspace)/2;
//		if( data.values.length%2==0 ){
//			
//		}else{
//			var length = data.values.length;
//			marginTop = (height - (length * rectBgheight + (length - 1) * rectBgspace))/2;
//		}
		
		marginTop=marginTop<20?20:marginTop;
		var margin = {
			top : marginTop,
			right : 10,
			bottom : 0,
			left : 10
		};

//		var svgWidth = width - margin.left - margin.right;
//		var svgHeight = height - margin.top - margin.bottom;

		$(id).empty();
		
		var svg = d3.select(id)
			.append("svg")
			.attr("class", "gatherProce")
			.attr("width", width )
			.attr("height", height)
			

		var g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		var cards = g.selectAll(".backGround").data(data.values);
			
		var cardsG = cards.enter().append("g");
		
		var rectBgWidth=Math.floor(width  * 0.7 -margin.left-margin.right - 60);    										//进度条长度
		
		
		
		
		
		cardsG.append("rect")
			.attr("class", "backGround")
			.attr("width", rectBgWidth)
			.attr("height", rectBgheight)
			.attr("fill", "#C0C0C0")
			.attr("stroke", "#8B7D7B")
			.attr("stroke-width","1")
			.attr("x", function(d,i){
				return 44;
				
			})
			.attr("y", function(d,i){
				return i * rectBgspace ;
			});
		
		
		var usageRect=cardsG.append("rect")
							.attr("class","usageRect")
							.attr("width", function(d){
								var used = d.total - d.free;
								return used / d.total * rectBgWidth;
							})
							.attr("height", rectBgheight)
							.attr("fill", function(d){
								var used = d.total - d.free;
								var flag = (used / d.total).toFixed(3) <= 0.900;
								if(flag) return "#25a0d9";
								return "#d9524e";
							})
							.attr("x", function(d,i){
								return 44;	
							})
							.attr("y", function(d,i){
								return i * rectBgspace ;
							});
		
		cardsG.append("text")
			.attr("font-weight", "bold")
			.attr("font-size", "10px")
			.text(function(d){
				return d.path;
			})
			.attr("fill", "#fff")
			.attr("x", function(d,i){
				return rectBgheight * 2 > 50 ? rectBgheight * 2 : 50;
			})
			.attr("y",function(d,i){
				return i * rectBgspace + rectBgheight/2 + 5;
			});
		
		cardsG.append("circle")
			.attr("cx", 25)
	    	.attr("cy", function(d, i){
				return i * rectBgspace + rectBgheight/2;
	    	})
	    	.attr("r", 30)
	    	.attr("stroke", function(d){
				var used = d.total - d.free;
				var flag = (used / d.total).toFixed(3) <= 0.900;
				if(flag) return "#25a0d9";
				return "#d9524e";
			})
	    	.attr("stroke-width", "2px")
	    	.attr("fill", "rgb(28, 56, 94)");
		
		cardsG.append("text")
		  .attr("class","diskUseRate")
		  .attr("font-size", "13px")
		  .text(function(d){
			  if(isNaN(d.total)){
				  return " ";
			  }else{
				  var used = d.total - d.free;
				  return (used / d.total * 100).toFixed(1) + "%";
			  }
		  })
		  .attr("fill", "#fff")
		  .attr("text-anchor", "end")
		  .attr("x", 50)
		  .attr("y",function(d,i){
				return i * rectBgspace + rectBgheight/2 + 5;
		  });
		
		//创建表格
		g.append("text")
			.attr("font-size", "13px")
			.text('剩余')
			.attr("fill", "rgb(0, 160, 233)")
			.attr("text-anchor", "middle")
			.attr("x", rectBgWidth + 60 + width * 0.3 / 4)
			.attr("y", -5);
		
		g.append("text")
			.attr("font-size", "13px")
			.text('总量')
			.attr("fill", "rgb(0, 160, 233)")
			.attr("text-anchor", "middle")
			.attr("x", rectBgWidth + 62 + (width * 0.3 / 2) * 1.5)
			.attr("y", -5);
		
		cardsG.append("rect")
			.attr("width", width * 0.3 / 2)
			.attr("height", rectBgheight)
			.attr("fill", "#363636")
			.attr("x", rectBgWidth + 60)
			.attr("y", function(d,i){
				return i * rectBgspace;
			});
		
		cardsG.append("text")
			.attr("font-size", "13px")
			.text(function(d){
				if(isNaN(d.free)||d.unit==null){
					return " ";
				}else{
					return d.free+d.unit;
				}
			})
			.attr("fill", "#FFFFFF")
			.attr("text-anchor", "middle")
			.attr("x", rectBgWidth + 60 + width * 0.3 / 4)
			.attr("y", function(d,i){
				return i * rectBgspace + rectBgheight/1.5;
			});
		
		cardsG.append("rect")
			.attr("width", width * 0.3 / 2)
			.attr("height", rectBgheight)
			.attr("fill", "#363636")
			.attr("x", rectBgWidth + 62 + width * 0.3 / 2)
			.attr("y", function(d,i){
				return i * rectBgspace;
			});
		
		cardsG.append("text")
			.attr("font-size", "13px")
			.text(function(d){
				if(isNaN(d.total)||d.unit==null){
					return " ";
				}else{
					return d.total+d.unit;
				}
			})
			.attr("fill", "#FFFFFF")
			.attr("text-anchor", "middle")
			.attr("x", rectBgWidth + 62 + (width * 0.3 / 2) * 1.5)
			.attr("y", function(d,i){
				return i * rectBgspace + rectBgheight/1.5;
			});
		 

		
		
		this.getSvg = function() {
			var svgD = new Object();
			// svgD['svg'] = svg;
			svgD['cards'] = g;
			svgD['rectBgWidth'] = rectBgWidth;
			return svgD;
		}
	}
	
	function redraw(reloadData, cards,rectBgWidth) {
		cards.data(reloadData.values).enter();
		cards.selectAll(".usageRect")
			  .data(reloadData.values)
                .attr("fill", function(d,i){
                    var used = d.total - d.free;
                    var flag = (used / d.total).toFixed(3) <= 0.900;
                    if(flag) return "rgba(0, 160, 233, 1)";
                    return "red";
                })
			  .transition()
			  .duration(1000)
			  .attr("width", function(d,i){
				  var used = d.total - d.free;
				  return used / d.total * rectBgWidth;
			  })

		cards.selectAll(".diskDescription")
			  .data(reloadData.values)
			  .text(function(d){
				  var free = (d.free).toFixed(1);
				  return "可用 "+ free +"G,共 "+ d.total +"G";
			  })
			  
		cards.selectAll(".diskUseRate")
			  .data(reloadData.values)
			  .text(function(d){
				  if(isNaN(d.total)||isNaN(d.free)){
					  return " ";
				  }else{
					  var used = d.total - d.free;
					  return (used / d.total * 100).toFixed(1) + "%";
				  }
			  })
	}
	
	$.ajax({
		url : url,
		type : "POST",
        data: params,
        dataType: "json",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
		success : function(result) {
			if(result["titleTime"]!=undefined){
				document.getElementById('disk').innerHTML="磁盘使用情况("+result["titleTime"]+")";
			}else{
				document.getElementById('disk').innerHTML="磁盘使用情况";
			}
			if( result["resultData"] == undefined ){
				console.log("数据获取失败,失败原因：接口返回为空!");
				return;
			}
			var directorUsage=new generate(result["resultData"], id);
			clearInterval(intervalID_directorUsage);
			intervalID_directorUsage = setInterval(function() {
				$.ajax({
					url : url,
					type : 'post',
					success : function(reloadResultObj) {
						if(result["titleTime"]!=undefined){
							document.getElementById('disk').innerHTML="磁盘使用情况("+result["titleTime"]+")";
						}else{
							document.getElementById('disk').innerHTML="磁盘使用情况";
						}
						var reloadResult = reloadResultObj["result"];
						if ("fail" == reloadResult) {
							console.log("数据获取失败,失败原因：" + reloadResultObj["message"]);
						} else {
//							$(id).empty();
//							directorUsage=new generate(reloadResultObj["resultData"], id);
							var svg=directorUsage.getSvg();
							redraw(reloadResultObj["resultData"],svg['cards'],svg['rectBgWidth'])
						}
					}
				})
			}, 60*1000*10);
		}
	});
}

