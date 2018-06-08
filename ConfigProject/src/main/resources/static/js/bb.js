var plan;
var des;
$(document).ready(function(){
	plan="1";
	$("#button_accommodationNeeds").click(function(){
	    //  alert(plan);
		$("#div_accommodationNeeds").show();
		//从后台取出酒店信息并显示
		$.ajax({
		      type: "POST",
		      url: "/synet/TourOption_2",
		      datatype:"json",
  	          data: JSON.stringify({"plan":plan,"des":des}),
		      headers: {
		          "Content-Type": "application/json; charset=utf-8"
		      },
		      success: function (map) {
		    	  var table_hotel = "<tr><td>option</td><td>类型</td><td>价格</td><td>剩余数量</td></tr>";
			      	
			      	$.each(map,function(key,values){
			      		console.log(values)
			  		 	table_hotel += "<tr><td><input name='decision_2' type='radio' value='"+values.value+"'/></td><td>"+values.name+"</td><td>"+values.price+"</td><td>"+values.remnant+"</td></tr>";
			  		})
			  			
			  		table_hotel+="<tr><td></td><td></td><td></td><td><input id='submit_2' type='button' value='Yes'/></td></tr>";
			      $("#roomifo").html(table_hotel);
			      
			      //点击提交按钮获取提交的值，在通过提交的值从后台去到相关于他的信息，展示在页面上
			      $("#submit_2").click(function(){
		      		var option_2= $("input[name='decision_2']:checked").val()+"";
		      		
		      	    $("#div_accommodationNeeds").hide();
		      	    
		      		  $.ajax({
		      	          type: "POST",
		      	          url: "/synet/TourOption_3",
		      	          datatype:"json",
		      	          data: JSON.stringify({"option_2":option_2,"plan":plan,"des":des}),
		      	          headers: {
		      	              "Content-Type": "application/json; charset=utf-8"
		      	          },
		      	          success: function (r) {
		      	        	var table_feeDetail_hotel="<tr><td>酒店费用:<span id='price_hotel'>"+r.price+"</span></td></tr>";
		      	        	$("#fee_detail_hotel").html(table_feeDetail_hotel);
		      	        	$("#fee_detail_hotel1").html(table_feeDetail_hotel);

		      	        	var train=parseInt($("#price_train").text());
		      	        	if(train == "" || train == undefined || train == null){
		      	        		train=0;
		      	        	}
		      	        	var hotel=parseInt(r.price);
//		      	        	alert(r.price);
		      	        	var total=train+hotel;
		      	        	var price_charge=parseInt(total*0.03);
		      	        	var price_total=total+price_charge;
		      	        	var table_total="<tr><td>手续费:"+price_charge+"</td></tr>";
		      	        		table_total+="<tr><td>实际费用:"+price_total+"</td></tr>";
		      	        	$("#fee").html(table_total);
		      	        	$("#fee1").html(table_total);
		      	        	
		      	        	$("#BeijingToXIan_hotel").html(r.name+"地址:"+r.address);
		      	        	$("#BeijingToXIan_hotel").show();
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
	})

    function adjustHeightOfPage(pageNo) {

        var offset = 80;
        var pageContentHeight = 0;

        var pageType = $('div[data-page-no="' + pageNo + '"]').data("page-type");

        if( pageType != undefined && pageType == "gallery") {
            pageContentHeight = $(".cd-hero-slider li:nth-of-type(" + pageNo + ") .tm-img-gallery-container").height();
        }
        else {
            pageContentHeight = $(".cd-hero-slider li:nth-of-type(" + pageNo + ") .js-tm-page-content").height();
        }
        if($(window).width() >= 992) { offset = 120; }
        else if($(window).width() < 480) { offset = 40; }

        // Get the page height
        var totalPageHeight = 15 + $('.cd-slider-nav').height()
            + pageContentHeight + offset
            + $('.tm-footer').height();

        // Adjust layout based on page height and window height
        if(totalPageHeight > $(window).height())
        {
            $('.cd-hero-slider').addClass('small-screen');
            $('.cd-hero-slider li:nth-of-type(' + pageNo + ')').css("min-height", totalPageHeight + "px");
        }
        else
        {
            $('.cd-hero-slider').removeClass('small-screen');
            $('.cd-hero-slider li:nth-of-type(' + pageNo + ')').css("min-height", "100%");
        }
    }

    /*
        Everything is loaded including images.
    */
    $(window).load(function(){

        adjustHeightOfPage(1); // Adjust page height

        /* Gallery One pop up
        -----------------------------------------*/
        $('.gallery-one').magnificPopup({
            delegate: 'a', // child items selector, by clicking on it popup will open
            type: 'image',
            gallery:{enabled:true}
        });

        /* Gallery Two pop up
        -----------------------------------------*/
        $('.gallery-two').magnificPopup({
            delegate: 'a',
            type: 'image',
            gallery:{enabled:true}
        });

        /* Gallery Three pop up
        -----------------------------------------*/
        $('.gallery-three').magnificPopup({
            delegate: 'a',
            type: 'image',
            gallery:{enabled:true}
        });

        /* Collapse menu after click
        -----------------------------------------*/
        $('#tmNavbar a').click(function(){
            $('#tmNavbar').collapse('hide');

            adjustHeightOfPage($(this).data("no")); // Adjust page height
        });

        /* Browser resized
        -----------------------------------------*/
        $( window ).resize(function() {
            var currentPageNo = $(".cd-hero-slider li.selected .js-tm-page-content").data("page-no");

            // wait 3 seconds
            setTimeout(function() {
                adjustHeightOfPage( currentPageNo );
            }, 1000);

        });

        // Remove preloader (https://ihatetomatoes.net/create-custom-preloading-screen/)
        $('body').addClass('loaded');

    });




});