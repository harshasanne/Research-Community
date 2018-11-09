let username = $('#username').val();
let author = $('#author').val();

$('#follow-btn').click(function(){
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
