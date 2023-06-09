import React, { useEffect, useState } from 'react';
import './Cart.css';
import api from "../api/apiConfig";

import { useNavigate } from 'react-router-dom';
import CartData from './CartData';

const Cart = ({ modal, handleModalContentClick, cartProduct, email, isLoggedIn }) => {

    const navigate = useNavigate();

    const [total, updateTotal] = useState(0);

    useEffect(() => {
        const quantities = cartProduct && cartProduct.map(product => product.quantity);
        const totalPrice = cartProduct && cartProduct.reduce((total, product, index) => {
            return total + product.price * quantities[index];
        }, 0);
        updateTotal(totalPrice);
    }, [cartProduct]);

    const removeAllProductsFromCart = async () => {
        const token = localStorage.getItem('jwt');
        api.delete('api/products/cart/delete-all', {
            headers: {
                Authorization: token
            }
        })
            .then(response => {
                console.log(response);
            })
            .catch(error => {
                console.error(error);
            })
    }

    const navigateToCheckout = () => {
        navigate("/checkout");
        document.body.classList.remove('modal-open');
    }

    return (
        <div>
            {modal && (
                <div className={`modal ${modal ? 'active' : ''}`}>
                    <div className="modal-content" onClick={handleModalContentClick}>
                        {isLoggedIn ? <>
                            <div className='cart-header'>
                            <h6>Cart ({cartProduct.length})</h6>
                            {cartProduct.length !== 0 &&
                                <p><button className='remove-all-btn btn' onClick={removeAllProductsFromCart}>Remove all</button></p>
                            }
                        </div>
                        <div className='cart-main'>
                            <div>
                                {cartProduct && cartProduct.map((product, index) =>
                                    <CartData key={index} product={product} updateTotal={updateTotal} total={total} email={email}/>
                                )}
                            </div>
                        </div>
                        {cartProduct.length !== 0 &&
                            <div className='cart-price'>

                                <h6>Total</h6>
                                <h6>₹ {total.toFixed(2)}
                                </h6>
                            </div>
                        }
                        <div>
                            {cartProduct.length !== 0 && <button className='checkout-btn btn primary-btn' onClick={navigateToCheckout}>Checkout</button>}
                        </div>
                        </> : <div>Kindly sign in to check the products</div>}
                        
                    </div>
                    <span className={`overlay ${modal ? 'active' : ''}`}></span>
                </div>
            )}
        </div>
    )
}

export default Cart;