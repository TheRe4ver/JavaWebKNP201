/* Script for SPA page + AUTH functions */
document.addEventListener("DOMContentLoaded", () => {
    M.Modal.init(document.querySelectorAll('.modal'), {
        opacity: 0.6,
        inDuration: 200,
        outDuration: 200,
        onOpenStart: onModalOpens
    });

    const authSignInButton = document.getElementById("auth-sign-in");
    if(authSignInButton) {
        authSignInButton.addEventListener("click", authSignInButtonClick);
    } else {
        console.error("auth-sign-in not found");
    }
    // Token verification
    const spaTokenStatus = document.getElementById("spa-token-status");
    if(spaTokenStatus) {
        const token = window.localStorage.getItem('token');
        if(token) {
            const tokenObj = JSON.parse(atob(token));
            spaTokenStatus.innerText = "Дійсний до " + tokenObj.exp;
            // TODO: перевірити на правильність декодування та дійсність
            const appContext = getAppContext();
            fetch(`${appContext}/tpl/spa-auth.html`, {
                method: 'GET',
                headers: {
                    'Authorization' : `Bearer ${token}`
                }
            })
                .then(r => r.text()).then( t =>
                    document.querySelector('auth-part').innerHTML = t );

            document.getElementById("spa-log-out")
                .addEventListener('click', logoutClick);

/* hw
            // const spaPage1Button = document.getElementById("spa-page-1");
            //
            // if(spaPage1Button) {
            //     spaPage1Button.addEventListener('click', () => {
            //         fetch(`${appContext}/tpl/secret-page.html`)
            //             .then(r => r.text()).then(t =>
            //             document.querySelector('auth-part').innerHTML = t);
            //     });
            // }
            //
            // const spaPage2Button = document.getElementById("spa-page-2");
            //
            // if(spaPage2Button) {
            //     spaPage2Button.addEventListener('click', () => {
            //         fetch(`${appContext}/tpl/secret-page-another.html`)
            //             .then(r => r.text()).then(t =>
            //             document.querySelector('auth-part').innerHTML = t);
            //     });
            // }
            //
            // const notFound = document.getElementById("404");
            //
            // if(notFound) {
            //     notFound.addEventListener('click', () => {
            //         fetch(`${appContext}/tpl/404.html`)
            //             .then(r => r.text()).then(t =>
            //             document.querySelector('auth-part').innerHTML = t);
            //     });
            // }
 */
        }
        else {
            spaTokenStatus.innerText = 'Не встановлено';
        }
    }
    const spaGetDataButton = document.getElementById("spa-get-data");
    if(spaGetDataButton) {
        spaGetDataButton.addEventListener('click', spaGetDataClick);
    }
});
function getAppContext() {
    return '/' + window.location.pathname.split('/')[1];
}
function spaGetDataClick() {
    console.log('spaGetDataClick');
    const appContext = getAppContext();
    fetch(`${appContext}/tpl/NP.png`, {
        method: 'GET',
        headers: {
            'Authorization' : `Bearer ${window.localStorage.getItem('token')}`
        }
    })
        .then(r => r.blob()).then( b => {
        const blobURL = URL.createObjectURL(b);
        document.querySelector('auth-part').innerHTML += `<img src=${blobURL} width="100"/>`;
    });
}
function logoutClick() {
    window.localStorage.removeItem('token');
    window.location.reload();
}
function onModalOpens() {
    [authLogin, authPassword, authMessage] = getAuthElements();
    authLogin.value = '';
    authPassword.value = '';
    authMessage.innerText = '';
}
function getAuthElements() {
    const authLogin = document.getElementById("auth-login");
    if(!authLogin) {
        throw '#auth-login not found';
    }
    const authPassword = document.getElementById("auth-password");
    if(!authPassword) {
        throw '#auth-password not found';
    }
    const authMessage = document.getElementById("auth-message");
    if(!authMessage) {
        throw '#auth-message not found';
    }
    return [authLogin, authPassword, authMessage];
}
function authSignInButtonClick() {
    [authLogin, authPassword, authMessage] = getAuthElements();

    if(authLogin.value.length === 0) {
        authMessage.innerText = "Логін не може бути порожнім";
    }

    const appContext = getAppContext();

    fetch(`${appContext}/auth?login=${authLogin.value}&password=${authPassword.value}`, {
        method: 'GET'
    }).then(r => {
        if(r.status !== 200) {
            authMessage.innerText = "Автентифікацію відхилено";
        }
        else {
            r.text().then(base64encodedText => {
                // console.log(base64encodedText);
                // base64 decoding
                const token = JSON.parse(atob(base64encodedText));
                if(typeof token.jti === 'undefined') {
                    authMessage.innerText = "Помилка одержання токену";
                    return;
                }
                window.localStorage.setItem('token', base64encodedText);
                // window.localStorage.setItem('exp', token['exp']);
                window.location = `${appContext}/spa`;
            });
        }
    })
}