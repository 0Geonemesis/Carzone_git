const sesion =
    JSON.parse(
        localStorage.getItem("sesion")
    );

if(!sesion){

    window.location =
        "login.html";
}

function cerrarSesion(){

    localStorage.removeItem("sesion");

    window.location =
        "login.html";
}

function verUsuarios(){

    if(sesion.rol !== "ADMIN"){

        alert(
            "No tiene permisos"
        );

        return;
    }

    const usuarios =
        JSON.parse(
            localStorage.getItem("usuarios")
        );

    let tabla = `
        <table border="1">

        <tr>
            <th>ID</th>
            <th>Usuario</th>
            <th>Rol</th>
        </tr>
    `;

    usuarios.forEach(u => {

        tabla += `
        <tr>
            <td>${u.id}</td>
            <td>${u.usuario}</td>
            <td>${u.rol}</td>
        </tr>
        `;
    });

    tabla += "</table>";

    document.getElementById(
        "tablaUsuarios"
    ).innerHTML = tabla;
}