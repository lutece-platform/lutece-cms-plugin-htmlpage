var _baseUrl = document.getElementsByTagName('base')[0].href;

function setBaseUrl( baseUrl ){
    if( baseUrl != '' ){
        _baseUrl = baseUrl;
    }
}

/**
 * Inserts sanitized HTML into an element: native Sanitizer API, else DOMPurify, else plain text.
 */
function setSafeHtml( element, html ){
    if( typeof element.setHTML === 'function' ){
        element.setHTML( html );
    } else if( window.DOMPurify ){
        element.innerHTML = DOMPurify.sanitize( html );
    } else {
        element.textContent = html;
    }
}

function getContentHtmlPage( id, idDefault, idDiv, divClass ){
    var url = _baseUrl + "rest/htmlpage-api/v1/htmlpage/" + encodeURIComponent(id);
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
        if( !response.ok ){
            throw new Error( 'HTTP ' + response.status );
        }
        return response.json();
    })
    .then(function(data) {
        var divElement = document.getElementById(idDiv);
        if ( data.status == 'OK' && divElement ) {
            if( divClass ){
                divElement.classList.add( divClass );
            }
            setSafeHtml( divElement, data.result );
        }
    })
    .catch(function(error) {
        console.error('Error:', error);
    });
}
