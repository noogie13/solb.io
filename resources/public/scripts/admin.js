function enliven(id, cookie) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "/enliven");

    var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "id");
    hiddenField.setAttribute("value", id);
    form.appendChild(hiddenField);

    document.body.appendChild(form);
    form.submit();
}
