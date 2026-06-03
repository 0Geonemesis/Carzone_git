if(!localStorage.getItem("usuarios")){

    const usuarios = [

        {
            id: 1,
            usuario: "admin",
            password: "123",
            rol: "ADMIN"
        },

        {
            id: 2,
            usuario: "vendedor",
            password: "123",
            rol: "VENDEDOR"
        }

    ];

    localStorage.setItem(
        "usuarios",
        JSON.stringify(usuarios)
    );
}