var width  = 1000;
var height = 970;
var z = 5;
var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height)
    .attr("z-index","5")
    .append("g")
    .attr("transform", "translate(50,20)");

var projection = d3.geo.mercator()
    .center([107, 31])
    .scale(850)
    .translate([width/2, height/2]);

var path = d3.geo.path()
    .projection(projection);


var color = d3.scale.category20();


d3.json("../js/china.geojson", function(error, root) {

    if (error)
        return console.error(error);
    console.log(root.features);

    svg.selectAll("path")
        .data( root.features )
        .enter()
        .append("path")
        .attr("stroke","#000")
        .attr("stroke-width",1)
        .attr("fill", function(d,i){
            return color(i);
        })
        .attr("d", path )
        .on("mouseover",function(d,i){
            d3.select(this)
                .attr("fill","yellow");
        })
        .on("mouseout",function(d,i){
            d3.select(this)
                .attr("fill",color(i));
        })
        .on("click",function (d,i) {
            alert(d.properties.name)
        })
        .on("mouseenter",function (d,i) {
            $("#ifoTable").fadeIn(0.001);
            var locat=d.properties.name;
            $.ajax({
                type: "POST",
                url: "/synet/TourOption_6",
                data:locat,
                headers: {
                    "Content-Type": "application/json; charset=utf-8"
                },
                success: function (r) {
                    var ispan="<span>&nbsp;&nbsp;&nbsp;"+r+"</span>";
                    $("#ifoSpan").html(ispan);
                },
                error: function (err) {
                    alert(err);
                    console.log(err.message)
                }
            });
         })
        .on("mouseleave",function (d,i) {
            $("#ifoTable").fadeOut(0.001);
         });
});