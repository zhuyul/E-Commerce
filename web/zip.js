function changeAddress(){
    var zipcode = $('#input_zip').val();  
    $.post('zip', {zipcode: zipcode}, function(data){
        var state = data[0];
        var city = data[1];
        document.getElementById('zip_city').value=city;
        document.getElementById('zip_state').value=state;
    }, "json");
}
$('#input_zip').on("change keyup", function(){
    changeAddress();
});