var alarmData;// 告警数据
$(function(){
	//获取告警分类
	$.ajax({
		url: "/alarmBrowse/getAlarmType",
		dataType: "json",
		type: "post",
		success: function(data){
			if (data.result != 'success'){
				alert(data.message);
			}else{
				$("#alarmTypeNode").append(data.resultData);
				// 绑定事件
				debugger;
				bindEvent();
			}
		},
		error: function(data){}
	});
	
	// 获取所有的告警事件
	getAlarmByAjax();
	
})

// 数据加载后才能绑定的事件
function bindEvent(){
	//告警流量伸缩
	$(".alarmType").click(function(){
		debugger;
		var display = $(this).parent().parent().find("ul").css("display");
		if (display == "none"){
			$(this).parent().parent().find("ul").show();
		}else{
			$(this).parent().parent().find("ul").hide();
		}
	});
	
	// 绑定显示列的事件
	$("#showColumn").change(function(){
		var ob=$("#selectSpan").html();
		var obj=ob.split(", ");
	    var s="<tr>";
	    for(var i=0; i<obj.length; i++){
				if(obj[i].trim()=="告警类型"){
					s+="<td class='s1'>告警类型</td>";
				}else if(obj[i].trim()=="等级"){
					s+="<td class='s2'>等级</td>";
				}else if(obj[i].trim()=="第一次发生时间"){
					s+="<td >第一次发生时间</td>";
				}else if(obj[i].trim()=="告警对象"){
					s+="<td class='s3'>告警对象</td>";
				}else if(obj[i].trim()=="告警地址"){
					s+="<td class='s5'>告警地址</td>";
				}else if(obj[i].trim()=="处理状态"){
					s+="<td class='s4'>处理状态</td>";
				}else if(obj[i].trim()=="告警描述"){
					s+="<td>告警描述</td>";
				}else if(obj[i].trim()=="解决方式"){
					s+="<td>解决方式</td>";
				}
		}
	    s += "</tr>";
	    for(var j=0;j<alarmData.length;j++) {
	        var value = alarmData[j];
	        s += "<tr>";
	        for (var i = 0; i < obj.length; i++) {
	        	debugger;
	            if (obj[i] == "告警类型") {
	                s += "<td class='s1'>" + value.type + "</td>";
	            } else if (obj[i] == "等级") {
	                s += "<td class='s2'>" + value.level + "</td>";
	            } else if (obj[i] == "第一次发生时间") {
	                var e = value.occur_time;
	                s += "<td>" + e + "</td>";
	            } else if (obj[i] == "告警对象") {
	                s += "<td class='s3'>" + value.name + "</td>";
	            } else if (obj[i] == "告警地址") {
	                s += "<td class='s5'>" + value.address + "</td>";
	            } else if (obj[i] == "处理状态") {
	                s += "<td class='s4'>" + value.stats + "</td>";
	            } else if (obj[i] == "告警描述") {
	                s += "<td>" + value.desc + "</td>";
	            } else if (obj[i] == "解决方式") {
	                s += "<td>" + value.cause + "</td>";
	            }
	        }
	 		s+="</tr>";
	 	  }
	 	  $("#table_1").html(s);
	});
	
	// 选中查看时间事件
	$("#selectTime").change(function(){
		var intervalTimeStr = $(this).find("option:selected").val();	// 获取查看的时间
		var showType = $("#selectType").find("option:selected").val();
		getAlarmByAjax(intervalTimeStr, showType);
	});
	
	// 选中查看分类事件
	$("#selectType").change(function(){
		var intervalTimeStr = $("#selectTime").find("option:selected").val();	// 获取查看的时间
		var showType = $(this).find("option:selected").val();	// 获取查看的时间
		var flag = null;
		$.ajax({
			url: "/alarmBrowse/getAllAlarm",
			data:{"intervalTimeStr":intervalTimeStr, "showType":showType},
			dataType: "json",
			type: "post",
			success: function (data){
				var table_GJ = "<tr><td class='s1'>告警类型</td><td class='s2'>等级</td><td>第一次发生时间</td><td class='s3'>告警来源</td><td>告警描述</td><td>解决方式</td></tr>";
	       	  	for(var i =0;i<data.length;i++){ 
	       	  		var value = data[i]; 
	       	  		if(showType == "type"){       			
	       	  			if(flag !== value.type){
	       	  				table_GJ += "<tr style='background-color:#e4e4e4'><td height='2px' colspan='10'></td></tr>"
	       	  				flag = value.type;
	       	  			}
	       	  		}else if(showType == "level"){       			
	       	  			if(flag !== value.level){       				
	       	  				table_GJ += "<tr style='background-color:#e4e4e4'><td height='2px' colspan='10'></td></tr>"
	       	  				flag = value.level;
	       	  			}
	       	  		}else if(showType == "name"){       			
	       	  			if(flag !== value.name){       				
		       				table_GJ += "<tr style='background-color:#e4e4e4'><td height='2px'colspan='10'></td></tr>"
	       					flag = value.name;
	       	  			}
	       	  		}
	       	  		table_GJ += "<tr></td><td class='s1'>"+value.type+"</td><td class='s2'>"+value.level+"</td><td>"+value.occur_time+"</td><td class='s3'>"+value.name+"</td><td>"+value.desc+"</td><td>"+value.cause+"</td></tr>";
	       	  	}
	       	  	$("#table_1").html(table_GJ);
	       	  	if(showType == "type"){
	       	  		$('.s1').css('background-color','#e6ffec');
	       	  	}else if(showType == "level"){
	       	  		$('.s2').css('background-color','#e6ffec');
	       	  	}else if(showType == "name"){
	       	  		$('.s3').css('background-color','#e6ffec');
	       	  	}
			},
			error: function(data){
				
			}
		});
	});
}

// 分类显示
function showByType(){
	var p=null;
    var objS = document.getElementById("select_2");
    var grade = objS.options[objS.selectedIndex].value;
    var w=grade;
	var e=grade;
	$.ajax({
        type: "POST",
        url: "/gjt_1/Ifo3",
        datatype:"json",
        data: JSON.stringify({"e":e,"deals":deals}),
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (list) {
        	debugger;
        	 var table_GJ = "<tr><td class='s1'>告警类型</td><td class='s2'>等级</td><td>第一次发生时间</td><td class='s3'>告警来源</td><td class='s5'>告警地址</td><td class='s4'>处理状态</td><td>告警描述</td><td>解决方式</td></tr>";
       	  for(var i =0;i<list.length;i++){ 
       		  var value = list[i]; 
       		  if(w==1){       			
       			  if(p!==value.type){
       				table_GJ+="<tr style='background-color:#e4e4e4'><td height='2px' colspan='10'></td></tr>"
       				p=value.type;
       			  }
       		  }else if(w==2){       			
       			if(p!==value.gread){       				
       				table_GJ+="<tr style='background-color:#e4e4e4'><td height='2px' colspan='10'></td></tr>"
       				p=value.gread;
       			  }
       		  }else if(w==3){       			
       			if(p!==value.from){       				
       				table_GJ+="<tr style='background-color:#e4e4e4'><td height='2px'colspan='10'></td></tr>"
       				p=value.from;
       			  }
       		  }else if(w==4){
       			if(p!==value.stats){      				
       				table_GJ+="<tr style='background-color:#e4e4e4'><td height='2px'colspan='10'></td></tr>"
       				p=value.stats;
       			  }
       		  }else if(w==5){       			
       			if(p!==value.address){      						
       				table_GJ+="<tr style='background-color:#e4e4e4'><td height='2px'colspan='10'></td></tr>"
       				p=value.address;
       			  }
       		  }
       		  var t=new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');
       		  table_GJ+="<tr></td><td class='s1'>"+value.type+"</td><td class='s2'>"+value.gread+"</td><td>"+t+"</td><td class='s3'>"+value.from+"</td><td class='s5'>"+value.address+"</td><td class='s4'>"+value.stats+"</td><td>"+value.discrib+"</td><td>"+value.deal+"</td></tr>";
       	  }
       	  $("#table_1").html(table_GJ);
       	  if(w==1){
       		  $('.s1').css('background-color','#e6ffec');
       	  }else if(w==2){
       		  $('.s2').css('background-color','#e6ffec');
       	  }else if(w==3){
       		  $('.s3').css('background-color','#e6ffec');
       	  }else if(w==4){
       		  $('.s4').css('background-color','#e6ffec');
       	  }else if(w==5){
       		  $('.s5').css('background-color','#e6ffec');
       	  }
       },
       error: function (err) {
           alert(err);
           console.log(err.message)
       }
  });	
}

function getAlarmByAjax(intervalTimeStr, showType){
	debugger;
	// 获取告警事件
	$.ajax({
		url: "/alarmBrowse/getAllAlarm",
		data:{"intervalTimeStr":intervalTimeStr, "showType":showType},
		dataType: "json",
		type: "post",
		success: function (data){
			debugger;
			alarmData = data;
			var table_GJ = "<tr><td>告警类型</td><td>等级</td><td>第一次发生时间</td><td>告警对象</td><td>告警描述</td><td>解决方式</td></tr>";
            for (var i =0;i < data.length;i++){
                var value = data[i];
//                var e=new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');
                table_GJ+="<tr><td>"+value.type+"</td><td>"+value.level+"</td><td>"+value.occur_time
                	+"</td><td>"+value.name+"</td><td>"+value.desc+"</td><td>"+value.cause+"</td></tr>";
            }
            $("#table_1").html(table_GJ);
		},
		error: function(data){
			
		}
	});
}
