function login(){

    const usuario =
        document.getElementById("usuario").value;

    const password =
        document.getElementById("password").value;

    const usuarios =
        JSON.parse(
            localStorage.getItem("usuarios")
        );

    const encontrado =
        usuarios.find(u =>
            u.usuario === usuario &&
            u.password === password
        );

    if(!encontrado){

        alert(
            "Usuario o contraseña incorrectos"
        );

        return;
    }

    localStorage.setItem(
        "sesion",
        JSON.stringify(encontrado)
    );

    if(encontrado.rol === "ADMIN"){

        window.location =
            "admin.html";

    }else{

        window.location =
            "vendedor.html";
    }
}