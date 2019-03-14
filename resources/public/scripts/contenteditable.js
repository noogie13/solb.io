function getContentEditable(get, call){
// this function is used to get the stuff inside the contenteditable elements, to be used in the form to edit posts
        document.getElementById(call).value = document.getElementById(get).innerHTML;
}

function getContentEditableAll() {
    getContentEditable('unhappy','content');
    getContentEditable('sad','title');
    getContentEditable('depressed','tags');
    getContentEditable('rip','forward');
}

window.onload=function() {
    var contentedit = document.getElementById('unhappy');
    contentedit.addEventListener("keydown", insertTabAtCaret);
    contentedit.addEventListener("keydown", insertNewLineAtCaret);
}

function insertTabAtCaret(event){
    if(event.keyCode === 9){
        event.preventDefault();
        var range = window.getSelection().getRangeAt(0);

        var tabNode = document.createTextNode("\u00a0\u00a0\u00a0\u00a0");
        range.insertNode(tabNode);

        range.setStartAfter(tabNode);
        range.setEndAfter(tabNode);
    }
}

function insertNewLineAtCaret(event){
    if(event.keyCode === 13){
        event.preventDefault();
        var selection = window.getSelection(),
            range = selection.getRangeAt(0),
            br = document.createTextNode('\n');
        range.deleteContents();
        range.insertNode(br);
        range.setStartAfter(br);
        range.setEndAfter(br);
        range.collapse(false);
        selection.removeAllRanges();
        selection.addRange(range);
    }
}
// document.querySelector("unhappy").addEventListener("keydown",insertTabAtCaret);
