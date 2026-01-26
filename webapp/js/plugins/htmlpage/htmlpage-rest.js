var _baseUrl = document.getElementsByTagName('base')[0].href;

function setBaseUrl( baseUrl ){
    if( baseUrl != '' ){
        _baseUrl = baseUrl;
    }
}

function getContentHtmlPage( id, idDefault, idDiv, divClass ){
    var url = _baseUrl + "rest/htmlpage-api/v1/htmlpage/" + id;
    if( idDefault ){
        url += "?id_default=" + encodeURIComponent(idDefault);
    }
    
    fetch(url, {
        method: "GET",
        headers: {
            "Accept": "application/json"
        }
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(data) {
        if ( data.status == 'OK' ) {
            var divElement = document.getElementById(idDiv);
            if( divClass != undefined ){ 
                divElement.classList.add('htmlpage');
            }
            divElement.innerHTML = data.result;
        }
    })
    .catch(function(error) {
        console.error('Error:', error);
    });
}