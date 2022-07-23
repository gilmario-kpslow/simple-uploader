const uploaderInput = document.getElementById('uploaderInput');
const tabela = document.getElementById('versoes');
const tabelaArquivos = document.getElementById('tabelaArquivos');
const versaoButton = document.getElementById('versaoButton');
const inputVersao = document.getElementById('inputVersao');
const token = localStorage.getItem('acess-token');

let versao;
let versoes = [];

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

const selecionaVersao = (v) => {
    versao = v;
    inputVersao.value = v;
    uploaderInput.classList.remove('d-none');
    listarArquivosVersao(v);
    document.getElementById('profile-tab').click();
};

const deselecionaVersao = () => {
    versao = '';
    inputVersao.value = '';
    uploaderInput.classList.add('d-none');
    tabelaArquivos.innerHTML = '';

};

const listarArquivosVersao = (versao) => {
    showLoader();
    const req = new Request(`uploader/versao/${versao}`, {
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
            montarTabelaVersoes(d);
            hideLoader();
        });
    }).catch((e) => {
        error(e);
        hideLoader();
    });
};

const publicar = (versao) => {
    showLoader();
    const req = new Request(`uploader/publicar/${versao}`, {
        method: 'PUT',
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
            messagem('ok', 'ok');
            hideLoader();
        });
    }).catch((e) => {
        error(e);
    });
    ;
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

const montarTabelaVersoes = (arq) => {
    tabelaArquivos.innerHTML = '';
    arq.forEach((v, i) => {
        const tr = document.createElement('tr');
        const tdId = document.createElement('td');
        tdId.innerHTML = i + 1;
        tr.appendChild(tdId);

        const tdNome = document.createElement('td');
        tdNome.innerHTML = v.nome;
        tr.appendChild(tdNome);

        const tdAcao = document.createElement('td');

        tdAcao.appendChild(creatButtonAcao("fa-trash", "Excluir Versão", (e) => deleteArquivo(versao, v.nome)));
        tr.appendChild(tdAcao);
        tabelaArquivos.appendChild(tr);
    });
};

const montaTabela = (versoes) => {
    tabela.innerHTML = '';
    versoes.forEach((v, i) => {
        const tr = document.createElement('tr');
        const tdId = document.createElement('td');
        tdId.innerHTML = i + 1;
        tr.appendChild(tdId);

        const tdNome = document.createElement('td');
        tdNome.innerHTML = v.nome;
        tr.appendChild(tdNome);

        const tdAcao = document.createElement('td');
        const btnAcao = document.createElement('button');

        tdAcao.appendChild(creatButtonAcao('fa-eye', 'Selecionar', () => selecionaVersao(v.nome)));
        tdAcao.appendChild(creatButtonAcao("fa-trash", "Excluir Versão", (e) => deleteVersao(v.nome)));
        tdAcao.appendChild(creatButtonAcao('fa-check', 'Publicar versão', () => publicar(v.nome)));
        tr.appendChild(tdAcao);
        tabela.appendChild(tr);
    });

};

const arrayBufferToBase64 = (buffer) => {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[ i ]);
    }
    return btoa(binary);
};

const error = (e) => {
    console.log(e);
    hideLoader();
    messagem('ERRO', e, 'DANGER');
};

const criarVersao = (e) => {
    showLoader();
    const req = new Request(`uploader/versao`, {
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
            selecionaVersao(d.resumo);
            getVersoes();
            hideLoader();
        });
    }).catch((e) => {
        error(e);
    });
    ;
};

const deleteVersao = (versao) => {
    showLoader();
    const req = new Request(`uploader/versao/${versao}`, {
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
            deselecionaVersao();
            getVersoes();
            messagem(d.tipo, d.detalhe);
            hideLoader();
        });
    }).catch((e) => {
        error(e);
    });
    ;
};

const deleteArquivo = (versao, fileName) => {
    showLoader();
    const req = new Request(`uploader/versao/${versao}/${fileName}`, {
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
            selecionaVersao(versao);
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

const getVersoes = () => {
    showLoader();
    const req = new Request('uploader/listar', {
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
        resp.json().then(d => {
            versoes = d;
            versoes.sort(compare);
            montaTabela(versoes);
            hideLoader();
        });
    }).catch((e) => {
        error(e);
    });
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
                'Authorization': `Bearer ${token}`,
                'versao': versao
            },
            body: data
        });
        fetch(req).then((resp) => {
            resp.json().then(d => {
                messagem(d.detalhe, d.resumo, d.tipo);
                hideLoader();
                listarArquivosVersao(versao);
                uploaderInput.value = '';
            }).catch((e) => {
                error(e);
            });
        }).catch((e) => {
            error(e);
        });
    }

});

versaoButton.addEventListener('click', criarVersao);

getInfo();

getVersoes();

