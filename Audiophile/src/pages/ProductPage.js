import React, { useEffect } from 'react'
import { useParams } from 'react-router-dom'

import Header from "../components/Header";
import About from "../components/About";
import Categories from "../components/Categories";
import Footer from "../components/Footer";
import OtherProducts from '../components/OtherProducts';
import ProductDetails from '../components/ProductDetails';

const ProductPage = ({ getProductBySlug, product, cartProduct, addProductToCart }) => {

    const params = useParams();
    const slug = params.slug;

    useEffect(() => {
        getProductBySlug(slug);
    }, [getProductBySlug, slug]);

    return (
        <div className={slug}>
            <Header cartProduct={cartProduct}/>
            {product && (
                <ProductDetails product={product} addProductToCart={addProductToCart}/>
            )}
            {product && (
                <OtherProducts product={product}/>
            )}
            <Categories />
            <About />
            <Footer />
        </div>
    )
}

export default ProductPage;