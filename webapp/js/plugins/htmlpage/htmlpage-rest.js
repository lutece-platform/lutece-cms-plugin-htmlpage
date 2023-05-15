var _baseUrl = document.getElementsByTagName('base')[0].href;

function setBaseUrl( baseUrl )
{
	if( baseUrl != '' ){
		_baseUrl = baseUrl;
	}
}

function getContentHtmlPage( id, idDefault, idDiv, divClass ){
    var url = _baseUrl + "rest/htmlpage-api/v1/htmlpage/" + id;
    var data = {id_default:idDefault};
    $.ajax({
        url: url,
        type: "GET",
        dataType: "json",
        data: data,
        success: function (data) {
            if ( data.status == 'OK' ) {
				if( divClass != undefined ){ $("#"+ idDiv ).addClass( 'htmlpage' ) };
            	$( "#"+ idDiv ).html( data.result ); 
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    });   
}