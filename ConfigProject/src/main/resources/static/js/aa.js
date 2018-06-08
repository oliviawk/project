$(document).ready(function(){
//当点击方案二的小地图时，切换按钮位置切换背景方案地图
	des=$("#destination_option").text();
	// alert(des);
	if(des=="西安"){
	plan="1";
	$("#plan2").click(function(){
		$("#T_div3").css("background","url(../img/Tour2.png)");
		$("#T_div3").css('background-size','100% 100%');
		$('#button_accommodationNeeds').attr('class','button_accommodationNeeds_2');
//		$('.BeijingToXIan_start').attr('class','')
//		$('.BeijingToXIan_process').attr('class','')
//		$('.BeijingToXIan_arrive').attr('class','')
		$('#BeijingToXIan_hotel').attr('class','BeijingToXIan_hotel_1')
		plan="2";
		$('#BeijingToXIan_start').hide();
		$('#BeijingToXIan_process').hide();
		$('#BeijingToXIan_arrive').hide();
		$('#BeijingToXIan_hotel').hide();
	
});
	$("#plan1").click(function(){
		$("#T_div3").css("background","url(../img/Tour1.png)");
		$("#T_div3").css('background-size','100% 100%');
		$('#button_accommodationNeeds').attr('class','button_accommodationNeeds');
//		$('.BeijingToXIan_start').attr('class','')
//		$('#BeijingToXIan_process').attr('class','BeijingToXIan_process_plane');
//		$('.BeijingToXIan_arrive').attr('class','')
		$('#BeijingToXIan_hotel').attr('class','BeijingToXIan_hotel');
		plan="1";
		$('#BeijingToXIan_start').hide();
		$('#BeijingToXIan_process').hide();
		$('#BeijingToXIan_arrive').hide();
		$('#BeijingToXIan_hotel').hide();
		
	
});
	$("#plan3").click(function(){
		$("#T_div3").css("background","url(../img/Tour3.png)");
		$("#T_div3").css('background-size','100% 100%');
		$('#button_accommodationNeeds').attr('class','button_accommodationNeeds_2');
//		$('.BeijingToXIan_start').attr('class','')
		$('#BeijingToXIan_process').attr('class','BeijingToXIan_process_plane');
//		$('.BeijingToXIan_arrive').attr('class','')
		$('#BeijingToXIan_hotel').attr('class','BeijingToXIan_hotel_1');
		plan="3";
		$('#BeijingToXIan_start').hide();
		$('#BeijingToXIan_process').hide();
		$('#BeijingToXIan_arrive').hide();
		$('#BeijingToXIan_hotel').hide();
	
		});
	}else if(des=="天津"){
		plan="1";
		$('#plan1').attr('class','plan1_tianjin');
		$("#T_div3").css("background","url(../img/Tour1_tianjin.png)");
		$("#T_div3").css('background-size','100% 100%');
		$('#button_accommodationNeeds').attr('class','button_accommodationNeeds_tianjin');
		$('#button_choosetrain').attr('class','button_choosetrain_tianjin');
		$('#BeijingToXIan_hotel').attr('class','BeijingToXIan_hotel_1_tianjin');
		$('#BeijingToXIan_start').attr('class','BeijingToXIan_start_1_tianjin');
		$('#BeijingToXIan_process').attr('class','BeijingToXIan_process_tianjin');
		$('#BeijingToXIan_arrive').attr('class','BeijingToXIan_arrive_tianjin');
	}	
		
  $("#button_choosetrain").click(function(){
		 $("#div_choosetrain").show();
		
		 $.ajax({
		      type: "POST",
		      url: "/synet/TourOption_1",
		      datatype:"json",
  	          data: JSON.stringify({"des":des,"plan":plan}),
		      headers: {
		          "Content-Type": "application/json; charset=utf-8"
		      },
		      success: function (map) {
		      	var tableHtml = "<tr><td>option</td><td>火车班次</td><td>出发时间</td><td>车票价格</td></tr>";
		      	
		      	$.each(map,function(key,values){
		      		console.log(values)
	
		  		 	tableHtml += "<tr><td><input name='decision' type='radio' value='"+values.name+"'/></td><td>"+values.name+"</td><td>"+values.time+"</td><td>"+values.price+"</td></tr>";
		  		})
		      	tableHtml+="<tr><td></td><td></td><td></td><td><input id='submit_1' type='button' value='Yes'/></td></tr>";
		      
		      	
		      	$("#chang").html(tableHtml);
		      	
		      	 $("#submit_1").click(function(){
		      		 var option= $("input[name='decision']:checked").val();

		      	    $("#div_choosetrain").hide();
		      		  $.ajax({
		      	          type: "POST",
		      	          url: "/synet/TourOption",
		      	          datatype:"json",
		      	          data: JSON.stringify({"option":option,"plan":plan,"des":des}),
		      	          headers: {
		      	              "Content-Type": "application/json; charset=utf-8"
		      	          },
		      	          success: function (r) {
		      	        	  
		      	        	$("#BeijingToXIan_start").html(r.name+"&nbsp&nbsp"+r.time);
		      	        	$("#BeijingToXIan_start").show();
		      	        	$("#BeijingToXIan_process").html("全程需要:"+r.useTime+" 请注意休息");
		      	        	$("#BeijingToXIan_process").show();
		      	        	$("#BeijingToXIan_arrive").html("到达时间:"+r.arriveTime);
		      	        	$("#BeijingToXIan_arrive").show();
		      	        	
		      	        	var table_feeDetail="<tr><td>费用明细</td></tr>";
		      	        	table_feeDetail+="<tr><td>交通费:<span id='price_train'>"+r.price+"</span></td></tr>";
		      	        	$("#fee_detail").html(table_feeDetail);
		      	        	$("#fee_detail1").html(table_feeDetail);

		      	        	var hotel=parseInt($("#price_hotel").text());
		      	        	if(hotel == "" || hotel == undefined || hotel == null){
		      	        		hotel=0;
		      	        	}
		      	        	var train=parseInt(r.price);
//		      	        	alert(r.price);
		      	        	var total=train+hotel;
		      	        	var price_charge=parseInt(total*0.03);
		      	        	var price_total=total+price_charge;
		      	        	var table_total="<tr><td>手续费:"+price_charge+"</td></tr>";
	      	        			table_total+="<tr><td>实际费用:"+price_total+"</td></tr>";
		      	        	$("#fee").html(table_total);
		      	         },
		      	         error: function (err) {
		      	             alert(err);
		      	             console.log(err.message)
		      	         }
		      	    });
		      	  });
		     },
		     error: function (err) {
		         alert(err);
		         console.log(err.message)
		     }
	 });
	  
});
  
});