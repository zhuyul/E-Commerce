
function submitContent(){
	var form = document.getElementsByName("myForm")[0];
	var input = document.getElementById("inputs").value;
	var subject = document.getElementById("subject").value;
	if(input == " " || subject == " "){
		alert("Please finish form.");
	}
	else{
		document.location.href = "MAILTO:jungkyup@uci.edu?subject="+ subject +"&body=" + input;
	}
}



function resetContent(){
	if (confirm('Are you sure you want to reset?')) {
    	document.getElementById("inputs").value = "";
    	document.getElementById("subject").value = "";
	} 
}
