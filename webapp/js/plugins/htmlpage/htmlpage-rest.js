var _id;
var _idDefault;
var _idDiv;
var _baseUrl = document.getElementsByTagName('base')[0].href;

function init( id, idDefault, idDiv ){
	_id = id;
	_idDefault = idDefault;
	_idDiv = idDiv;
}

function setBaseUrl( baseUrl )
{
	if( baseUrl != '' ){
		_baseUrl = baseUrl;
	}
}

function getContentHtmlPage(  ){
    var url = _baseUrl + "rest/htmlpage-api/v1/htmlpage/" + _id;
    var data = {id_default:_idDefault};
    $.ajax({
        url: url,
        type: "GET",
        dataType: "json",
        data: data,
        success: function (data) {
            if ( data.status == 'OK' ) {
            	$("#"+ _idDiv ).html( data.result );
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    });   
}