async function checkUsername(){
    let user = document.getElementById("UserId").value;
    let url='http://localhost:8080/demo/CHeckUserName?uname='+user;
    fetch(url)  
    .then(response => response.json())
    .then(data => openDiv(data))
}
async function openDiv(data){
    var res = await data;
    console.log(res);
    if(res.status=="400"){
        reg();
    }
    else{
        alert('UserName already taken');
    }
}


function reg(){
    let user = document.getElementById("UserId").value;
    let pass = document.getElementById("pass").value;
    let conPass = document.getElementById("conPass").value;
    let ques = document.getElementById("ques").value;
    let ans = document.getElementById("ans").value;
    console.log(user);
    if(user==""){
        alert('User feild is empty');
    }
    else if (pass=="") {
        alert('Password feild is empty');
    } 
    else if(conPass==""){
        alert('Confirm Password feild is empty');
    }
    else if(ques==""){
        alert('Security question feild is empty');
    }
    else if(ans==""){
        alert('Password feild is empty');
    }
    else if(pass!=conPass){
        alert('Password and Confirm Password not equal');
        
    }
    else{
        let url='http://localhost:8080/demo/register?uname='+user+'&pass='+pass+'&ques='+ques+'&ans='+ans;
        fetch(url)  
        .then(response => response.json())
        .then(data => addUser(data))
    }
    
}

async function addUser(data){
    var res = await data;
    if(res.status==200){
        alert('Registration sucessful');
        window.location.replace("http://localhost:8080/demo/");
    }
    else{
        alert('Registration failed');
        window.location.replace("http://localhost:8080/demo/register.html");
    }
}