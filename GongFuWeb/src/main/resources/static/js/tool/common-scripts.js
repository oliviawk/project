/*(function($, h, c) {  
    var a = $([]), e = $.resize = $.extend($.resize, {}), i, k = "setTimeout", j = "resize", d = j  
            + "-special-event", b = "delay", f = "throttleWindow";  
    e[b] = 350;  
    e[f] = true;  
    $.event.special[j] = {  
        setup : function() {  
            if (!e[f] && this[k]) {  
                return false  
            }  
            var l = $(this);  
            a = a.add(l);  
            $.data(this, d, {  
                w : l.width(),  
                h : l.height()  
            });  
            if (a.length === 1) {  
                g()  
            }  
        },  
        teardown : function() {  
            if (!e[f] && this[k]) {  
                return false  
            }  
            var l = $(this);  
            a = a.not(l);  
            l.removeData(d);  
            if (!a.length) {  
                clearTimeout(i)  
            }  
        },  
        add : function(l) {  
            if (!e[f] && this[k]) {  
                return false  
            }  
            var n;  
            function m(s, o, p) {  
                var q = $(this), r = $.data(this, d);  
                r.w = o !== c ? o : q.width();  
                r.h = p !== c ? p : q.height();  
                n.apply(this, arguments)  
            }  
            if ($.isFunction(l)) {  
                n = l;  
                return m  
            } else {  
                n = l.handler;  
                l.handler = m  
            }  
        }  
    };  
    function g() {  
        i = h[k](function() {  
            a.each(function() {  
                var n = $(this), m = n.width(), l = n.height(), o = $  
                        .data(this, d);  
                if (m !== o.w || l !== o.h) {  
                    n.trigger(j, [ o.w = m, o.h = l ])  
                }  
            });  
            g()  
        }, e[b])  
    }  
})(jQuery, this); 

*/
/*---LEFT BAR ACCORDION----*/
$(function() {
    $('#nav-accordion').dcAccordion({
        eventType: 'click',
        autoClose: true,
        saveState: true,
        disableLink: true,
        speed: 'slow',
        showCount: false,
        autoExpand: true,
//        cookie: 'dcjq-accordion-1',
        classExpand: 'dcjq-current-parent'
    });
   /* $("#page-wrapper").on('resize', function() {
    	console.info("common-scripts: page-wrapper resize!");
	});*/
});

var Script = function () {


//    sidebar dropdown menu auto scrolling

    jQuery('#sidebar .sub-menu > a').click(function () {
        var o = ($(this).offset());
        diff = 250 - o.top;
        if(diff>0)
            $("#sidebar").scrollTo("-="+Math.abs(diff),500);
        else
            $("#sidebar").scrollTo("+="+Math.abs(diff),500);
        
//        var href = i.attr("href");
//        if(href!=""){
//        	$("#external-frame").attr("src",href);
//        }
    });



//    sidebar toggle

    $(function() {
        function responsiveView() {
            var wSize = $(window).width();
            if (wSize <= 768) {
                $('#container').addClass('sidebar-close');
                $('#sidebar > ul').hide();
            }

            if (wSize > 768) {
                $('#container').removeClass('sidebar-close');
                $('#sidebar > ul').show();
            }
        }
        $(window).on('load', responsiveView);
        $(window).on('resize', responsiveView);
    });

    $('.fa-bars').click(function () {
        if ($('#sidebar > ul').is(":visible") === true) {
            $('#main-content').css({
                'margin-left': '0px'
            });
            $('#sidebar').css({
                'margin-left': '-210px'
            });
            $('#sidebar > ul').hide();
            $("#container").addClass("sidebar-closed");
        } else {
            $('#main-content').css({
                'margin-left': '170px'
            });
            $('#sidebar > ul').show();
            $('#sidebar').css({
                'margin-left': '0'
            });
            $("#container").removeClass("sidebar-closed");
        }
    });

// custom scrollbar
    $("#sidebar").niceScroll({styler:"fb",cursorcolor:"rgba(0,0,0,.2)", cursorwidth: '3', cursorborderradius: '10px', background: 'rgba(108,164,205,.15)', spacebarenabled:false, cursorborder: ''});

    $("html").niceScroll({styler:"fb",cursorcolor:"rgba(0,0,0,.2)", cursorwidth: '6', cursorborderradius: '10px', background: 'rgba(108,164,205,.15)', spacebarenabled:false,  cursorborder: '', zindex: '1000'});

// widget tools

    jQuery('.panel .tools .fa-chevron-down').click(function () {
        var el = jQuery(this).parents(".panel").children(".panel-body");
        if (jQuery(this).hasClass("fa-chevron-down")) {
            jQuery(this).removeClass("fa-chevron-down").addClass("fa-chevron-up");
            el.slideUp(200);
        } else {
            jQuery(this).removeClass("fa-chevron-up").addClass("fa-chevron-down");
            el.slideDown(200);
        }
    });

    jQuery('.panel .tools .fa-times').click(function () {
        jQuery(this).parents(".panel").parent().remove();
    });


//    tool tips

    $('.tooltips').tooltip();

//    popovers

    $('.popovers').popover();



// custom bar chart

    if ($(".custom-bar-chart")) {
        $(".bar").each(function () {
            var i = $(this).find(".value").html();
            $(this).find(".value").html("");
            $(this).find(".value").animate({
                height: i
            }, 2000)
        })
    }


}();