const loginButton = document.getElementById('loginButton');
const usernameInput = document.getElementById('usernameInput');
const passInput = document.getElementById('passInput');
const messagem = (titulo, messagem, tipo) => {
    const m = document.createElement('div');
    m.classList.add('alert');
    m.classList.add(tipo);
    const h5 = document.createElement('h6');
    h5.classList.add('alert-heading');
    h5.innerHTML = titulo;
    const detalhe = document.createElement('span');
    detalhe.innerHTML = messagem;
    m.appendChild(h5);
    m.appendChild(detalhe);
    const contem = document.getElementById('messagem');
    contem.appendChild(m);
    setTimeout(limparMessagem, 2000);
};
const limparMessagem = () => {
    const contem = document.getElementById('messagem');
    contem.innerHTML = '';
};
const invalido = (value) => {
    console.log(value);
    return value === null || value === undefined || value.length === '' || value.length < 6;
};
const login = () => {
    if (invalido(usernameInput.value)) {
        messagem("Campo username inválido.", "Campo obrigatório!", 'alert-danger');
        usernameInput.focus();
        return;
    }

    if (invalido(passInput.value)) {
        messagem("Campo password inválido.", "Campo obrigatório!", 'alert-danger');
        passInput.focus();
        return;
    }

    const req = new Request('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'text/plain'
        },
        body: `username=${usernameInput.value}&password=${passInput.value}`
    });

    fetch(req).then((resp) => {
        resp.text().then(d => {
            if (resp.status === 200) {
                localStorage.setItem('acess-token', d);
                location.href = '/';
            } else {
                messagem("Acesso inválido.", d, 'alert-danger');
            }
        });

    }).catch((error) => {
        messagem("Erro ao acessar.", error, 'alert-danger');
    });
};



loginButton.addEventListener('click', login);