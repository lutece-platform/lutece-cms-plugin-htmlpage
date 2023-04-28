var _baseUrl = document.getElementsByTagName('base')[0].href;

function setBaseUrl( baseUrl )
{
	if( baseUrl != '' ){
		_baseUrl = baseUrl;
	}
}

function getContentHtmlPage( id, idDefault, idDiv  ){
    var url = _baseUrl + "rest/htmlpage-api/v1/htmlpage/" + id;
    var data = {id_default:idDefault};
    $.ajax({
        url: url,
        type: "GET",
        dataType: "json",
        data: data,
        success: function (data) {
            if ( data.status == 'OK' ) {
            	$("#"+ idDiv ).html( data.result );
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    });   
}