const uploaderInput = document.getElementById('uploaderInput');
const uploaderDiv = document.getElementById('uploaderDiv');
const tipoDiv = document.getElementById('tipoDiv');

const tabelaArquivos = document.getElementById('tabelaArquivos');
const token = localStorage.getItem('acess-token');

const showLoader = () => {
    const loader = document.getElementById('loader');
    loader.classList.remove('d-none');
};

const hideLoader = () => {
    const loader = document.getElementById('loader');
    loader.classList.add('d-none');
};

const messagem = (titulo, messagem, tipo = 'SUCCESS') => {

    const m = document.createElement('div');
    m.classList.add('alert');
    m.classList.add(`alert-${tipo.toLowerCase()}`);
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

const listarArquivos = () => {
    showLoader();
    const req = new Request(`uploader/arquivos`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    fetch(req).then((resp) => {
        resp.json().then(d => {
            if (resp.status !== 200) {
                messagem('Error', 'Error', 'alert-danger');
                return;
            }
            montarTabelaArquivos(d);
            hideLoader();
        });
    }).catch((e) => {
        error(e);
        hideLoader();
    });
};

const creatButtonAcao = (icone, title, acao) => {
    const btn = document.createElement('i');
    btn.classList.add('fa');
    btn.classList.add(icone);
    btn.classList.add('cursor-pointer');
    btn.classList.add('border');
    btn.title = title;
    btn.addEventListener('click', acao);
    return btn;
};

const montarTabelaArquivos = (arq) => {
    tabelaArquivos.innerHTML = '';
    arq.forEach((v, i) => {
        const tr = document.createElement('tr');
        const tdId = document.createElement('td');
        tdId.innerHTML = i + 1;
        tr.appendChild(tdId);

        const tdNome = document.createElement('td');
        tdNome.innerHTML = v.nome;
        tr.appendChild(tdNome);

        const tdTamanho = document.createElement('td');
        tdTamanho.innerHTML = v.tamanho;
        tr.appendChild(tdTamanho);

        const tdAcao = document.createElement('td');
        tdAcao.appendChild(creatButtonAcao('fa-download', 'Download', () => download(v.nome)));
        tdAcao.appendChild(creatButtonAcao("fa-trash", "Excluir Versão", (e) => deleteArquivo(v.nome)));
        tr.appendChild(tdAcao);
        tabelaArquivos.appendChild(tr);
    });
};

const download = (finaName) => {
    const req = new Request(`uploader/download?filename=${finaName}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    fetch(req).then((resp) => {
        if (resp.status !== 200) {
            messagem('Error', 'Error', 'alert-danger');
            return;
        }
        resp.blob().then(d => {
            var url = window.URL.createObjectURL(d);
            var a = document.createElement('a');
            a.href = url;
            a.download = finaName;
            document.body.appendChild(a); // we need to append the element to the dom -> otherwise it will not work in firefox
            a.click();
            a.remove();

        });
    }).catch((e) => {
        error(e);
        hideLoader();
    });
};

const error = (e) => {
    console.log(e);
    hideLoader();
    messagem('ERRO', e, 'DANGER');
};

const deleteArquivo = (fileName) => {
    showLoader();
    const req = new Request(`uploader/arquivo/${fileName}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    fetch(req).then((resp) => {
        resp.json().then(d => {
            if (resp.status !== 200) {
                messagem('Error', 'Error', 'alert-danger');
                return;
            }
            listarArquivos();
            messagem(d.tipo, d.detalhe);
            hideLoader();
        });
    }).catch((e) => {
        error(e);
    });

};

const compare = (a, b) => {
    if (a.nome === b.nome) {
        return 0;
    }

    if (a.nome < b.nome) {
        return 1;
    }

    if (a.nome > b.nome) {
        return -1;
    }

};

const getInfo = () => {
    showLoader();
    const req = new Request('uploader', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    fetch(req).then((resp) => {
        if (resp.status !== 200) {
            location.href = '/login/index.html';
            return;
        }
        resp.json().then(d => {
            hideLoader();
        }).catch((e) => {
            error(e);
        });
    }).catch((e) => {
        error(e);
    });
};

uploaderInput.addEventListener('input', (e) => {
    showLoader();
    if (e.target.files && e.target.files.length > 0) {

        const data = new FormData();
        for (const file of e.target.files) {
            data.append('file', file, file.name);
        }

        const req = new Request('uploader/upload', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: data
        });
        fetch(req).then((resp) => {
            resp.json().then(d => {
                messagem(d.detalhe, d.resumo, d.tipo);
                hideLoader();
                uploaderInput.value = '';
                listarArquivos();
            }).catch((e) => {
                error(e);
            });
        }).catch((e) => {
            error(e);
        });
    }

});

getInfo();

listarArquivos();

