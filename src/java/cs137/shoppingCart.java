
package cs137;

import java.util.HashMap;


public class shoppingCart {
    
    HashMap< String, Integer> cartItems;
    
    public void change_quantity(String pid, Integer number){
        cartItems.put(pid, number);
    }
    
    public int return_quantity(String pid){
        return cartItems.get(pid);
    }
    
    public void deleteproduct(String pid){
        cartItems.remove(pid);
    }
    
    public shoppingCart(){
     cartItems = new HashMap<String,Integer>();
    }
    public HashMap<String, Integer> getCartItems(){
        return cartItems;
    }
    public void addproduct(String itemId){
        if(cartItems.containsKey(itemId)){
            int quantity =  cartItems.get(itemId);
            cartItems.put(itemId, quantity+1);
        }
        else{
            cartItems.put(itemId, 1);
        }
        
    }
}
