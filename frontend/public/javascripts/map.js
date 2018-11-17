function loadMap(ary) {

    var mapOptions = {
        zoom: 4,
        center: {lat: -25.344, lng: 131.036}
    };
    var map = new google.maps.Map(document.getElementById("map"), mapOptions);

    $('tr').each(function (a, b) {
        let title = $('.title', b).text();
        let address = $('.address', b).text();
        let credential = 'AIzaSyCnmczszk6HGSsc4JPJD4nhQK8HUXMumcA';
        let url = 'https://maps.googleapis.com/maps/api/geocode/json?address=' + address + '&key=' + credential;
        $.get(url, function( data ) {
            let lat = data.results[0].geometry.location.lat;
            let lng = data.results[0].geometry.location.lng;

            let latLng = new google.maps.LatLng(lat, lng);
            let marker = new google.maps.Marker({
                position: latLng,
                title: title
            });
            marker.setMap(map);
        });
    });
    //alert(JSON.stringify(ary));
}