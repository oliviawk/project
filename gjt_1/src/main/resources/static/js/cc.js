$(window).on('load', function () {
    $('#search_11').selectpicker({
        'selectedText': '设备CPU告警'
    });
});
$(window).on('load', function () {
    $('#search_22').selectpicker({
        'selectedText': '设备CPU告警'
    });
});
$(window).on('load', function () {
    $('#search_33').selectpicker({
        'selectedText': '设备CPU告警'
    });
});
$(window).on('load', function () {
    $('#search_44').selectpicker({
        'selectedText': '设备CPU告警'
    });
});
$(window).on('load', function () {
    $('#search_55').selectpicker({
        'selectedText': '设备CPU告警'
    });
});


var time;
var deals;
$(document).ready(function(){
    deals="1";
	dd();
});
$("#all").click(function(){
    dd();
    // $(select_1).option("全部");
});
$("#dealtwo").click(function(){
    deals="2";
    deal();
});
$("#dealone").click(function(){
    deals="1";
    deal();
});
$("#dealthree").click(function(){
    deals="3";
    deal();
});
$("#dealfour").click(function(){
    deals="4";
    deal();
});
function deal(){
    $.ajax({
        type: "POST",
        url: "/gjt_1/Ifo1",
        data:deals,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (list) {
        	debugger;
            var table_GJ = "<tr><td>告警类型</td><td>等级</td><td>第一次发生时间</td><td>告警对象</td><td>告警地址</td><td>处理状态</td><td>告警描述</td><td>解决方式</td></tr>";
            for(var i =0;i<list.length;i++){
                var value = list[i];
                var e=new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');


                table_GJ+="<tr><td>"+value.type+"</td><td>"+value.gread+"</td><td>"+e+"</td><td>"+value.from+"</td><td>"+value.address+"</td><td>"+value.stats+"</td><td>"+value.discrib+"</td><td>"+value.deal+"</td></tr>";
            }
            $("#table_1").html(table_GJ);
        },
        error: function (err) {
            alert(err);
            console.log(err.message)
        }
    });
}



function aa(){
	debugger;
    var objS = document.getElementById("select_1");
    var grade = objS.options[objS.selectedIndex].value;
	time=grade;
//	alert(time);
	$.ajax({
          type: "POST",
          url: "/gjt_1/Ifo2",
          datatype:"json",
          data: JSON.stringify({"time":time,"deals":deals}),
          headers: {
              "Content-Type": "application/json; charset=utf-8"
          },
          success: function (list) {
        	  debugger;
        	  var table_GJ = "<tr><td>告警类型</td><td>等级</td><td>第一次发生时间</td><td>告警对象</td><td>告警地址</td><td>处理状态</td><td>告警描述</td><td>解决方式</td></tr>";
        	  for(var i =0;i<list.length;i++){ 
        		  var value = list[i]; 
        		  var e=new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');
                  table_GJ+="<tr><td>"+value.type+"</td><td>"+value.gread+"</td><td>"+e+"</td><td>"+value.from+"</td><td>"+value.address+"</td><td>"+value.stats+"</td><td>"+value.discrib+"</td><td>"+value.deal+"</td></tr>";
        	  }
        	  $("#table_1").html(table_GJ);
         },
         error: function (err) {
             alert(err);
             console.log(err.message)
         }
    });
 }

function bb(){
	var p=null;
    var objS = document.getElementById("select_2");
    var grade = objS.options[objS.selectedIndex].value;
    var w=grade;
    console.log(grade);
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
function chk(){
	debugger;
	$.ajax({
        type: "POST",
        url: "/gjt_1/Ifo1",
        data:deals,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (list) {
        	debugger;
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
          console.log(s)
         for(var j=0;j<list.length;j++) {
             var value = list[j];
             // var e = new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/, ' ');
             s += "<tr>";
             for (var i = 0; i < obj.length; i++) {
                 if (obj[i] == "告警类型") {
                     s += "<td class='s1'>" + value.type + "</td>";
                 } else if (obj[i] == "等级") {
                     s += "<td class='s2'>" + value.gread + "</td>";
                 } else if (obj[i] == "第一次发生时间") {
                     var e = new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/, ' ');
                     s += "<td>" + e + "</td>";
                 } else if (obj[i] == "告警对象") {
                     s += "<td class='s3'>" + value.from + "</td>";
                 } else if (obj[i] == "告警地址") {
                     s += "<td class='s5'>" + value.address + "</td>";
                 } else if (obj[i] == "处理状态") {
                     s += "<td class='s4'>" + value.stats + "</td>";
                 } else if (obj[i] == "告警描述") {
                     s += "<td>" + value.discrib + "</td>";
                 } else if (obj[i] == "解决方式") {
                     s += "<td>" + value.deal + "</td>";
                 }
             }
      		s+="</tr>";
      	  }
      	  $("#table_1").html(s);
       },
       error: function (err) {
           alert(err);
           console.log(err.message)
       }
  });	 
}


function chk1(){
	$('#chsory').show();
}



function chk2(){
    var obj = ["连通性告警","设备CPU告警","主机内存告警","阈值警告"],obj1 = ["1","2","3"],obj2 = ["核心路由器","云资源","数据传送","数据共享"],obj3 = ["待处理","处理中","已处理"],obj4 = ["10.20.65.199","10.20.67.199","10.20.67.177","10.20.67.176"];
    var p = $("#search_11").val();
    if(typeof(p)!=="string"&&p!=null){
        var o = p.toString();
        obj=o.split(",");
    }
    var p1 = $("#search_22").val();
    if(typeof(p1)!=="string"&&p1!=null){
        var o1 =p1.toString();
        obj1=o1.split(",");
    }
    var p2 = $("#search_33").val();
    if(typeof(p2)!=="string"&&p2!=null){
        var o2 =p2.toString();
        obj2=o2.split(",");
    }
    var p3 = $("#search_44").val();
    if(typeof(p3)!=="string"&&p3!=null){
        var o3 =p3.toString();
        obj3=o3.split(",");
    }
    var p4 = $("#search_55").val();
    if(typeof(p4)!=="string"&&p4!=null){
        var o4 =p4.toString();
        obj4=o4.split(",");
    }

    $.ajax({
        type: "POST",
        url: "/gjt_1/Ifo1",
        data: deals,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (list) {
        	debugger;
            var table_GJ = "<tr><td>告警类型</td><td>等级</td><td>第一次发生时间</td><td>告警对象</td><td>告警地址</td><td>处理状态</td><td>告警描述</td><td>解决方式</td></tr>";
            for(var i = 0 ; i < list.length ; i++){
                var value = list[i];
                if(obj.indexOf(value.type) == -1){
                    list.splice(i,1);
                    i--;
                    continue;
                }
                if(obj1.indexOf(value.gread) == -1){
                    list.splice(i,1);
                    i--;
                    continue;
                }
                if(obj2.indexOf(value.from) == -1){
                    list.splice(i,1);
                    i--;
                    continue;
                }
                if(obj3.indexOf(value.stats) == -1){
                    list.splice(i,1);
                    i--;
                    continue;
                }
                if(obj4.indexOf(value.address) == -1){
                    list.splice(i,1);
                    i--;
                    continue;
                }
            }
            for(var i = 0 ; i < list.length ; i ++){
                var value = list[i];
                var e=new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');
                table_GJ+="<tr><td>"+value.type+"</td><td>"+value.gread+"</td><td>"+e+"</td><td>"+value.from+"</td><td>"+value.address+"</td><td>"+value.stats+"</td><td>"+value.discrib+"</td><td>"+value.deal+"</td></tr>";

            }

            $("#table_1").html(table_GJ);
        },
        error: function (err) {
            alert(err);
            console.log(err.message)
        }
    });
}
function dd(){
    $.ajax({
        type: "POST",
        url: "/gjt_1/Ifo1",
        data:deals,
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        success: function (list) {
        	debugger;
            var table_GJ = "<tr><td>告警类型</td><td>等级</td><td>第一次发生时间</td><td>告警对象</td><td>告警地址</td><td>处理状态</td><td>告警描述</td><td>解决方式</td></tr>";
            for(var i =0;i<list.length;i++){
                var value = list[i];
                var e=new Date(parseInt(value.occtime) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');
                table_GJ+="<tr><td>"+value.type+"</td><td>"+value.gread+"</td><td>"+e+"</td><td>"+value.from+"</td><td>"+value.address+"</td><td>"+value.stats+"</td><td>"+value.discrib+"</td><td>"+value.deal+"</td></tr>";
            }
            $("#table_1").html(table_GJ);
        },
        error: function (err) {
            alert(err);
            console.log(err.message)
        }
    });
}

