function changeQuantity(product_quantity_obj,product_id,single_price){
    if (product_quantity_obj.value <= 0){
        product_quantity_obj.value = 1;
    } else if (product_quantity_obj.value > 99) {
        product_quantity_obj.value = 99;
    }
    var product_quantity = product_quantity_obj.value;
    var former_total_price = $('#total_price').val();
    $.post('ajax', {product_quantity: product_quantity, product_id: product_id, single_price: single_price }, function(data){  
        var result =  Number(former_total_price)+ Number(data);
        $('#new_price').text(result.toFixed(2));
        $('#total_price').val(result);
    }, "json");
    
}

var product_quantities = document.getElementsByClassName("input_quantity");
var product_ids = document.getElementsByClassName("product_id");
var product_prices = document.getElementsByClassName("single_price");

for (i = 0; i < product_quantities.length; ++i) {
    product_quantities[i].addEventListener('input', 
                                        changeQuantity.bind(null
                                                    ,product_quantities[i]
                                                    ,product_ids[i].value
                                                    ,product_prices[i].value), false
                                                );
}
