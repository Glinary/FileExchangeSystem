async function join(userInput) {
    let userData = {
        userInput: userInput,
    }
    await fetch("/join", {
        method: "post",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData)
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
    })
    .then((data) => {
        console.log(data)
    })
    .catch((error) => {
        console.error('Error', error);
    })
}

function leave() {

}

function register() {
    
}

function storeFile(fileName) {

}

function getDirectory() {

}

function getFile(fileName) {

}

function getHelp() {
    helpContent = ```
    /join <server_ip_add> <port>
    /leave
    /register <handle>
    /store <filename>
    /dir
    /get <filename>
    /?
    ```;

    return helpContent;
}

const inputForm = document.getElementById('userInput');
// When user presses Enter key
userInput.addEventListener("keydown", async function(e) {
    if (e.key == 'Enter') {
        //get value from input box
        const data = userInput.value;
        console.log("Entered:", data);
        e.preventDefault();
        await join(data);
    }
})