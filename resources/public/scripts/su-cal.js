function getCal(cal, callback)
{
    const req = new XMLHttpRequest()
    var params = "cal="+encodeURI(cal)
    req.open("GET", "/su-cal-gen?"+params)
    req.send()
    req.onreadystatechange = function() {
        if(this.readyState==4 && this.status==200){
            callback(req.responseText) }}
}
