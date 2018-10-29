var list = document.getElementById("one").textContent;
var o = list.split(",");

var canvas = d3.select("body")
                        .append("svg")
                        .attr("width", 900)
                        .attr("height", 700)
                        // .append('g');
var circle = canvas.selectAll("circle")
    .data(o, function(d) { return d; });

    circle.enter().append("circle")
    .attr("cy", function(d, i) { return i * 30 + 40; })
    .attr("cx", function(d, i) { return i * 50 + 30; })
    .attr("r", function(d) { return 10; });
var text = canvas.selectAll("text")
    .data(o, function(d) { return d; })
    .enter()
    .append('text')
    .text(function(d,i) { return d; })
    .attr("x",function(d, i) { return i * 40 + 10; })
    .attr("y",function(d, i) { return i * 30 + 20;})

