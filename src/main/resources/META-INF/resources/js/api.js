const uploaderInput = document.getElementById('uploaderInput');
const tabela = document.getElementById('versoes');
const tabelaArquivos = document.getElementById('tabelaArquivos');
const versaoButton = document.getElementById('versaoButton');
const inputVersao = document.getElementById('inputVersao');
const token = localStorage.getItem('acess-token');

let versaoSelecionada;
let versoes = [];

const messagem = (titulo, messagem, tipo) => {
    const m = document.createElement('div');
    m.classList.add('alert');
    m.classList.add(tipo ? tipo : 'alert-success');
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
        });
    }).catch((error) => {
        console.log(error);
    });
};

const publicar = (versao) => {
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
            console.log(d);
            messagem('ok', 'ok');
        });
    }).catch((error) => {
        console.log(error);
    });
    ;
};

const creatButtonAcao = (nome, title, acao) => {
    const btn = document.createElement('button');
    btn.classList.add('btn');
    btn.classList.add('btn-default');
    btn.classList.add('border');
    btn.title = title;
    btn.innerHTML = nome;
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

        tdAcao.appendChild(creatButtonAcao("E", "Excluir Versão", (e) => deleteArquivo(versao, v.nome)));
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

        tdAcao.appendChild(creatButtonAcao('S', 'Selecionar', () => selecionaVersao(v.nome)));
        tdAcao.appendChild(creatButtonAcao("E", "Excluir Versão", (e) => deleteVersao(v.nome)));
        tdAcao.appendChild(creatButtonAcao('P', 'Publicar versão', () => publicar(v.nome)));
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

const upload = (name, base64) => {
    const obj = {nome: name, conteudo: base64, versao};
    const str = JSON.stringify(obj);

    const req = new Request('uploader/upload', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: str
    });

    fetch(req).then((resp) => {
        resp.json().then(d => {
            if (resp.status !== 200) {
                messagem('Error', 'Error', 'alert-danger');
                return;
            }
            messagem(d.tipo, d.detalhe);
            listarArquivosVersao(versao);
            uploaderInput.value = '';
        });
    }).catch((error) => {
        console.log(error);
    });
    ;
};

const read = (file) => {
    const fileReader = new FileReader();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = () => {
        const imageData = fileReader.result;
        const base64 = arrayBufferToBase64(imageData);
        upload(file.name, base64);
    };
};

const error = (e) => {
    console.log(e);
};

const criarVersao = (e) => {
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
        });
    }).catch((error) => {
        console.log(error);
    });
    ;
};

const deleteVersao = (versao) => {
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
        });
    }).catch((error) => {
        console.log(error);
    });
    ;
};

const deleteArquivo = (versao, fileName) => {
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
        });
    }).catch((error) => {
        console.log(error);
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
        });
    }).catch((error) => {
        console.log(error);
    });
};

const getInfo = () => {

    const req = new Request('uploader', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    fetch(req).then((resp) => {
        if (resp.status !== 200) {
            location.href = '/login';
            return;
        }
        resp.json().then(d => {
            console.log(d);
        }).catch((error) => {
            console.log(error);
        });
    }).catch((error) => {
        console.log(error);
    });
};

uploaderInput.addEventListener('input', (e) => {
    if (e.target.files && e.target.files.length > 0) {
        for (let i = 0; i < e.target.files.length; i++) {
            console.log(e.target.files[i]);
            read(e.target.files[i]);
        }
//        e.target.files.forEach((f) => {
//            console.log(f);
//            read(f);
//        });
//        read();
    }

});

versaoButton.addEventListener('click', criarVersao);

getInfo();

getVersoes();

