function registrar(){

    const usuario =
        document.getElementById("usuario").value;

    const password =
        document.getElementById("password").value;

    const rol =
        document.getElementById("rol").value;

    let usuarios =
        JSON.parse(
            localStorage.getItem("usuarios")
        );

    const existe =
        usuarios.find(
            u => u.usuario === usuario
        );

    if(existe){

        alert("Usuario ya existe");

        return;
    }

    const nuevoUsuario = {

        id: usuarios.length + 1,
        usuario,
        password,
        rol
    };

    usuarios.push(nuevoUsuario);

    localStorage.setItem(
        "usuarios",
        JSON.stringify(usuarios)
    );

    alert(
        "Usuario registrado correctamente"
    );

    window.location =
        "login.html";
}