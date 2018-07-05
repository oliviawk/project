var intervalTimeStr;// 告警数据
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
	
	var oTable = new TableInit();
	oTable.init();
	
	//选中查看时间范围，刷新表格
	$("#selectTime").change(function(){
		debugger;
		intervalTimeStr = $("#selectTime").find("option:selected").val();
		$("#alertBrowseTable").bootstrapTable("refresh");	//刷新表格
	});
	// 获取所有的告警事件
//	getAlarmByAjax();
	
})

var TableInit = function(){
	var oTableIint = new Object();
	//初始化Table
	oTableIint.init = function(){
		$("#alertBrowseTable").bootstrapTable({
			url: '/alarmBrowse/getAllAlarm',         //请求后台的URL（*）
			method: 'post',                      //请求方式（*）
			contentType : "application/x-www-form-urlencoded",
			showColumns:true,
			striped: true,                      //是否显示行间隔色
			cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
			pagination: true,                   //是否显示分页（*）
			queryParams: oTableIint.queryParams,//传递参数（*）
			sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
			pageNumber: 1,                       //初始化加载第一页，默认第一页
			pageSize: 8,                       //每页的记录行数（*）
			pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
			uniqueId: "no",                     //每一行的唯一标识，一般为主键列
			columns: [{
				field: 'name',
				title: '名称',
				sortable: true  
			},{
				field: 'type',
				title: '告警类型',
				sortable: true
			},{
				field: 'alertType',
				title: '告警等级',
				sortable: true
			},{
				field: 'occur_time',
				title: '第一次发生时间',
				sortable: true
			},{
				field: 'eventTitle',
				title: '告警描述',
				width: "50px",
				sortable: false
			}]
		});
	};
	
	//得到查询的参数
	oTableIint.queryParams = function (params) {
		debugger;
		var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
				limit: params.limit,   //页面大小
				offset:params.offset,
				sort: params.sort,
				order: params.order,
				intervalTimeStr: intervalTimeStr
		};
		return temp;
	};
	return oTableIint;
};

// 数据加载后才能绑定的事件
function bindEvent(){
	//告警类型伸缩
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
//	$("#selectTime").change(function(){
//		var intervalTimeStr = $(this).find("option:selected").val();	// 获取查看的时间
//		var showType = $("#selectType").find("option:selected").val();
//		getAlarmByAjax(intervalTimeStr, showType);
//	});
	
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
		},
		error: function(data){
			
		}
	});
}
