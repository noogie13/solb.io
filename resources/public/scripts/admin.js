function getRequest(url, callback)
{
    const req = new XMLHttpRequest()
    req.open("GET", url)
    req.send()
    req.onreadystatechange = function() {
        if(this.readyState==4 && this.status==200){
            callback(req.responseText) }}
}
