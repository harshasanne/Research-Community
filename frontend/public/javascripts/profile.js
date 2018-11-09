$('#follow-btn').click(function(){
    console.log($('#author').text());
    console.log($('#username').text())
    let username = $('#username').text();
    let author = $('#author').text();
    $.post("/follow",
        {
            username: username,
            author: author
        },
        function(data,status){
            location.reload();
            console.log("Data: " + data + "\nStatus: " + status);
        });
});

$('#unfollow-btn').click(function(){
    console.log($('#author').text());
    console.log($('#username').text())
    let username = $('#username').text();
    let author = $('#author').text();
    $.post("/unfollow",
        {
            username: username,
            author: author
        },
        function(data,status){
            location.reload();
            console.log("Data: " + data + "\nStatus: " + status);
        });
});
