document.addEventListener('DOMContentLoaded', function() {
    //var instances =


    // db.jsp
    const createButton = document.getElementById("db-create-button");
    if(createButton) createButton.addEventListener('click', createButtonClick);
    const insertButton = document.getElementById("db-insert-button");
    if(insertButton) insertButton.addEventListener('click', insertButtonClick);
    const readButton = document.getElementById("db-read-button");
    if(readButton) readButton.addEventListener('click', readButtonClick);
    const readDeletedButton = document.getElementById("db-read-deleted-button");
    if(readDeletedButton) readDeletedButton.addEventListener('click', readDeletedButtonClick);
});

function createButtonClick() {
    fetch(window.location.href, {
        method: 'PUT'
    }).then(r => r.json()).then(j => {
        console.log(j);
    })
}
function insertButtonClick() {
    const nameInput = document.querySelector('[name="user-name"]');
    if(! nameInput) throw '[name="user-name"] not found';
    const phoneInput = document.querySelector('[name="user-phone"]');
    if(! phoneInput) throw '[name="user-phone"] not found';
    fetch(window.location.href, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({name: nameInput.value, phone: phoneInput.value})
    }).then(r => r.json()).then(j => {
        console.log(j);
    });
}
function readButtonClick() {
    fetch(window.location.href, {
        method: "COPY",
        headers: {
            'Content-Type': 'application/json' // Set the content type to JSON
        }
    }).then(r => r.json()).then(showCalls);
}
function readDeletedButtonClick(e){
    fetch(window.location.href,{
        method: "PURGE"
    }).then(r => r.json()).then(showDeletedCalls);
}
function callClick(e) {
    // alert('CALLING id=' + e.target.getAttribute("data-id"));
    const callId = e.target.getAttribute("data-id");
    if(confirm(`Make call for order #${callId} ?` )){
        fetch(window.location.href + "?call-id=" + callId, {
            method: 'PATCH',
        }).then(r => r.json()).then(j => {
            if(typeof j.callMoment == 'undefined') {
                alert(j);
            }
            else {
                e.target.parentNode.innerHTML = j.callMoment;
            }
        });
    }
}
function deleteClick(e) {
    // alert('CALLING id=' + e.target.getAttribute("data-id"));
    const btn = e.target.closest('button');
    const callId = btn.getAttribute("data-id");
    if(confirm(`Delete order #${callId} ?` )){
        fetch(window.location.href + "?call-id=" + callId, {
            method: 'DELETE',
        }).then(r => {
            if( r.status === 202 ) {
                let tr = btn.parentNode.parentNode;
                tr.parentNode.removeChild(tr);
            } else {
                r.json().then(alert);
            }
        });
    }
}
function restoreClick(e){
    const btn = e.target.closest('button');
    const callId = btn.getAttribute("data-id");
    if(confirm(`RESTORE ORDER NUMBER ${callId}` )){
        fetch(window.location.href + "?call-id=" + callId, {
            method: 'MOVE',
        }).then(r => {
            if(r.status === 202){ // успішне видалення
                let tr =
                    btn          // button
                        .parentNode  // td
                        .parentNode; // tr
                tr.parentNode.removeChild(tr);
            }
            else{
                r.json().then(alert);
            }
        });
    }
}
function showCalls(j) {
    console.log(j);

    const table = document.getElementById("callMeTable");
    table.querySelector('tbody').innerHTML = '';
    j.forEach(call => {
        const row = table.querySelector('tbody').insertRow();
        row.insertCell(0).textContent = call.id;
        row.insertCell(1).textContent = call.name;
        row.insertCell(2).textContent = call.phone;
        row.insertCell(3).textContent = call.moment;
        row.insertCell(4).innerHTML = ( call.callMoment == null ) ? `<button class="btn green accent-4" data-id="${call.id}" onclick="callClick(event)">call</button>` : call.callMoment;
        row.insertCell(5).innerHTML = `<button class="btn green accent-4" data-id="${call.id}" onclick="deleteClick(event)"><i class="material-icons red-text">clear</i></button>`;
    });
    table.style.display = 'table';
}
function showDeletedCalls(j){
    console.log(j);

    const table = document.getElementById("callMeTable");

    table.querySelector('tbody').innerHTML = '';
    j.forEach(call => {
        const row = table.querySelector('tbody').insertRow();
        row.insertCell(0).textContent = call.id;
        row.insertCell(1).textContent = call.name;
        row.insertCell(2).textContent = call.phone;
        row.insertCell(3).textContent = call.moment;
        row.insertCell(4).innerHTML = ( call.callMoment == null ) ? `<button class="btn green accent-4" data-id="${call.id}" onclick="callClick(event)">call</button>` : call.callMoment;
        row.insertCell(5).innerHTML = ( call.deleteMoment == null )
            ? `<button class="btn green accent-4" data-id="${call.id}" onclick="deleteClick(event)"><i class="material-icons red-text">delete</i></button>`
            : `<button class="btn green accent-4" data-id="${call.id}" onclick="restoreClick(event)"><i class="material-icons">restore_from_trash</i></button>`;
    });

    table.style.display = 'table';
}