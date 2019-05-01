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
    // contentedit.addEventListener("keydown", insertNewLineAtCaret);
    contentedit.addEventListener("keydown", changeCodeBlock);
    contentedit.addEventListener("keydown", wrapTidbit);
    contentedit.addEventListener("keydown", wrapLink);
    contentedit.addEventListener("keydown", changeH3);
    contentedit.addEventListener("keydown", changeNormal);
}
function insertAfter(newNode, referenceNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}
function changeParent(newParent){
    var sel = window.getSelection(),
        container = sel.anchorNode.parentNode,
        emptyDiv = document.createElement("div");
    newParent.innerHTML = container.innerHTML;
    if (container.classList.contains("content")) {return 0;}
    container.replaceWith(newParent);
    var range = document.createRange();
    var sel = window.getSelection();
    range.setStart(newParent.childNodes[newParent.childNodes.length - 1],newParent.childNodes[0].length);
    range.collapse(true);
    sel.removeAllRanges();
    sel.addRange(range);
    // emptyDiv.appendChild(document.createElement("br"));
    // insertAfter(emptyDiv, newParent);
}
function changeCodeBlock(e){
    if(e.ctrlKey && e.which == 188){
        e.preventDefault();
        var precode = document.createElement("div"),
            sel = window.getSelection(),
            container = sel.anchorNode.parentNode;
        precode.classList.add("precode");
        changeParent(precode);
    }
}
function changeH3(e){
    if(e.ctrlKey && e.which ==  50){
        e.preventDefault();
        h3 = document.createElement("h3");
        changeParent(h3);
    }
}
function changeNormal(e){
    if(e.ctrlKey && e.which ==  49){
        e.preventDefault();

        div = document.createElement("div");
        changeParent(div);
    }
}
function wrapLink(e){
    if(e.ctrlKey && e.which == 76){
        e.preventDefault();

        var link = prompt("Link?", "https://www.google.com");
        var sel = window.getSelection();

        var a = document.createElement("a");
        a.setAttribute('href', link);
        if (window.getSelection) {

            if (sel.rangeCount) {
                var range = sel.getRangeAt(0).cloneRange();
                range.surroundContents(a);
                sel.removeAllRanges();
                sel.addRange(range);
            }
        }
    }
}
function wrapTidbit(e){
    if(e.ctrlKey && e.which == 83){
        e.preventDefault();
        var sel = window.getSelection();
        var tidbit = document.createElement("code");
        tidbit.classList.add("tidbit");
        if (window.getSelection) {
            if (sel.rangeCount) {
                var range = sel.getRangeAt(0).cloneRange();
                range.surroundContents(tidbit);
                sel.removeAllRanges();
                sel.addRange(range);
            }
        }
    }
}
function insertNewLineAtCaret(event){
    if(event.keyCode === 13){
        event.preventDefault();
        var selection = window.getSelection(),
            range = selection.getRangeAt(0),
            br = document.createElement('br');
        range.insertNode(br);
        range.setStartAfter(br);
        range.setEndAfter(br);
    }
}
function insertNewTopic(){
    var selection = window.getSelection(),
        range = selection.getRangeAt(0),
        newTopic = document.createTextNode("λλλ"),
        divver = document.createElement('div');
    divver.setAttribute('style', 'text-align: center; color: slategrey;');
    divver.appendChild(newTopic);


    range.insertNode(divver);
    range.setStartAfter(divver);
    range.setEndAfter(divver);
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
