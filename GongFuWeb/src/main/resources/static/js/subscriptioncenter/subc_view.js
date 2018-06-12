// module_relation_data
d3.json("../subscription/getdatamodulerelationView")
    .header("Content-Type","application/json;charset=UTF-8")
    .get(function(json) {
        console.info(json)
        function GroupExplorer(wrapper,config){
            var defaultConfig={
                data:{"nodes":[],"links":[]},
                width:window.innerWidth,
                height:window.innerHeight-17,
                distance:120
            };
            $.extend(true,defaultConfig,config);
            console.info(defaultConfig)
            defaultConfig.data.links.forEach(function (e) {
                if(typeof e.source!="number" && typeof e.target!="number"){
                    var sourceNode = defaultConfig.data.nodes.filter(function (n) {
                                return n.name === e.source;
                            })[0],
                        targetNode = defaultConfig.data.nodes.filter(function (n) {
                            return n.name === e.target;
                        })[0];
                    e.source = sourceNode;
                    e.target = targetNode;
                }
            });
            var _this=this,highlighted=null,dependsNode=[],dependsLinkAndText=[];
            var zoom = d3.zoom()
                .scaleExtent([0.5,10])
                .on("zoom",function(){
                    _this.zoomed();
                });

            this.vis = d3.select("body").append("svg:svg")
                .attr("width", defaultConfig.width)
                .attr("height", defaultConfig.height)
                .call(zoom).on("dblclick.zoom", null);

            this.vis=this.vis.append('g').attr('class','all')
                .attr("width", defaultConfig.width)
                .attr("height", defaultConfig.height);


            this.force = d3.forceSimulation()
                .nodes(defaultConfig.data.nodes)
                .force("link", d3.forceLink(defaultConfig.data.links).distance(defaultConfig.distance))
                .force("charge", d3.forceManyBody())
                .force("center", d3.forceCenter(defaultConfig.width / 2, defaultConfig.height / 2))
                .force("charge",d3.forceManyBody())
                .force("collide",d3.forceCollide(80).strength(0.5).iterations(10));

            this.vis.append("svg:defs").selectAll("marker")
                .data(["end"])
                .enter().append("svg:marker")
                .attr("id","arrow")
                .attr('class','arrow')
                .attr("viewBox", "0 -5 10 10")
                .attr("refX", 27)
                .attr("refY", 0)
                .attr("markerWidth", 9)
                .attr("markerHeight", 16)
                .attr("markerUnits","userSpaceOnUse")
                .attr("orient", "auto")
                .append("svg:path")
                .attr("d", "M0,-5L10,0L0,5")
                .attr('fill','#666');

            this.link = this.vis.selectAll("line.link")
                .data(defaultConfig.data.links)
                .enter().append("svg:line")
                .attr("class", "link")
                .attr('stroke-width',1)
                .attr("x1", function(d) {
                    return d.source.x;
                })
                .attr("y1", function(d) { return d.source.y; })
                .attr("x2", function(d) { return d.target.x; })
                .attr("y2", function(d) { return d.target.y; })
                .attr("marker-end","url(#arrow)")
                .attr('stroke','#999');

            var dragstart=function(d, i) {
                // console.info(d3.event.subject)
                _this.force.stop();
                d3.event.sourceEvent.stopPropagation();
            };

            var dragmove=function(d, i) {
                d.px += d3.event.dx;
                d.py += d3.event.dy;
                d.x += d3.event.dx;
                d.y += d3.event.dy;
                _this.tick();
            };

            var dragend=function(d, i) {
                d3.event.subject.fx = null;
                d3.event.subject.fy = null;
                _this.force.restart();
                _this.tick();
            };

            this.nodeDrag = d3.drag()
                .on("start", dragstart)
                .on("drag", dragmove)
                .on("end", dragend);


            this.highlightObject=function(obj){
                if (obj) {

                    var objIndex= obj.index;

                    dependsNode=dependsNode.concat([objIndex]);
                    dependsLinkAndText=dependsLinkAndText.concat([objIndex]);

                    defaultConfig.data.links.forEach(function(lkItem){
                        if(objIndex==lkItem['source']['index']){
                            dependsNode=dependsNode.concat([lkItem.target.index])
                        }else if(objIndex==lkItem['target']['index']){
                            dependsNode=dependsNode.concat([lkItem.source.index])
                        }
                    });

                    _this.node.classed('inactive',function(d){
                        return (dependsNode.indexOf(d.index)==-1)
                    });
                    _this.link.classed('inactive', function(d) {

                        return ((dependsLinkAndText.indexOf(d.source.index)==-1) && (dependsLinkAndText.indexOf(d.target.index)==-1))
                    });

                    _this.linetext.classed('inactive',function(d){
                        return ((dependsLinkAndText.indexOf(d.source.index)==-1) && (dependsLinkAndText.indexOf(d.target.index)==-1))
                    });
                } else {
                    _this.node.classed('inactive', false);
                    _this.link.classed('inactive', false);
                    _this.linetext.classed('inactive', false);
                }
            };

            this.highlightToolTip=function(obj){
                if(obj){
                    console.info(obj)
                    _this.tooltip.html("<div class='title'>"+obj.name+" 的资料</div>" +
                        "<table class='detail-info'>" +
                        "   <tr><td class='td-label'>数据/环节名：</td><td>"+obj.name+"</td></tr>" +
                        "   <tr><td class='td-label'>文件名：</td><td>"+obj.fileName+"</td></tr>" +
                        // "   <tr><td class='td-label'>链接：</td><td><a href='http://www.cnblogs.com/leyi'>韭菜茄子的博客</a></td></tr>" +
                        "</table>")
                        .style("left",(d3.event.pageX+20)+"px")
                        .style("top",(d3.event.pageY-20)+"px")
                        .style("opacity",1.0);
                }else{
                    _this.tooltip.style("opacity",0.0);
                }
            };

            this.tooltip=d3.select("body").append("div")
                .attr("class","tooltip")
                .attr("opacity",0.0)
                .on('dblclick',function(){
                    d3.event.stopPropagation();
                })
                .on('mouseover',function(){
                    if (_this.node.mouseoutTimeout) {
                        clearTimeout(_this.node.mouseoutTimeout);
                        _this.node.mouseoutTimeout = null;
                    }
                })
                .on('mouseout',function(){
                    if (_this.node.mouseoutTimeout) {
                        clearTimeout(_this.node.mouseoutTimeout);
                        _this.node.mouseoutTimeout = null;
                    }
                    _this.node.mouseoutTimeout=setTimeout(function() {
                        _this.highlightToolTip(null);
                    }, 300);
                });

            this.node = this.vis.selectAll("g.node")
                .data(defaultConfig.data.nodes)
                .enter().append("svg:g")
                .attr("class", "node")
                .call(_this.nodeDrag)
                .on('mouseover', function(d) {
                    if (_this.node.mouseoutTimeout) {
                        clearTimeout(_this.node.mouseoutTimeout);
                        _this.node.mouseoutTimeout = null;
                    }
                    _this.highlightToolTip(d);
                })
                .on('mouseout', function() {
                    if (_this.node.mouseoutTimeout) {
                        clearTimeout(_this.node.mouseoutTimeout);
                        _this.node.mouseoutTimeout = null;
                    }
                    _this.node.mouseoutTimeout=setTimeout(function() {
                        _this.highlightToolTip(null);
                    }, 300);
                })
                .on('dblclick',function(d){
                    _this.highlightObject(d);
                    d3.event.stopPropagation();
                });
            d3.select("body").on('dblclick',function(){
                dependsNode=dependsLinkAndText=[];
                _this.highlightObject(null);
            });


            this.node.append("svg:image")
                .attr("class", "circle")
                .attr("xlink:href", function (d) {
                    if (d.isNode){
                        return "../img/mobile.png";
                    }else {
                        return "../img/mobile.png";
                    }

                })
                .attr("x", "-15px")
                .attr("y", "-15px")
                .attr("width", "30px")
                .attr("height", "30px");

            this.node.append("svg:text")
                .attr("class", "nodetext")
                .attr("dy", "30px")
                .attr('text-anchor','middle')
                .text(function(d) { return d.name })

            this.linetext=this.vis.selectAll('.linetext')
                .data(defaultConfig.data.links)
                .enter()
                .append("text")
                .attr("class", "linetext")
                .attr("x",function(d){ return (d.source.x + d.target.x) / 2})
                .attr("y",function(d){ return (d.source.y + d.target.y) / 2})
                .text(function (d) {
                    return d.relation
                })
                .call(d3.drag());

            this.zoomed=function(){
                _this.vis.attr("transform", d3.event.transform);
            };


            var findMaxWeightNode=function(){
                var baseWeight= 1,baseNode;
                defaultConfig.data.nodes.forEach(function(item){
                    console.info(item)
                    if(item.weight>baseWeight){
                        baseWeight=item.weight
                        baseNode=item
                    }
                });
                return baseNode;
            };

            this.tick=function() {
                /*                    var findMaxWeightNodeIndex=findMaxWeightNode().index;
                                    defaultConfig.data.nodes[findMaxWeightNodeIndex].x = defaultConfig.width / 2;
                                    defaultConfig.data.nodes[findMaxWeightNodeIndex].y = defaultConfig.height / 2;*/
                _this.link.attr("x1", function(d) { return d.source.x; })
                    .attr("y1", function(d) { return d.source.y; })
                    .attr("x2", function(d) { return d.target.x})
                    .attr("y2", function(d) { return d.target.y;});
                _this.linetext.attr("x",function(d){ return (d.source.x + d.target.x) / 2})
                    .attr("y",function(d){ return (d.source.y + d.target.y) / 2});
                _this.node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
            };
            _this.force.on("tick", this.tick);

        }
        new GroupExplorer('body',{
            data:json
        });
    });