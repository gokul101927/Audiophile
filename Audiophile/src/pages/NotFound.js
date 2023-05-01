import React from 'react'

import Header from "../components/Header";
import Footer from "../components/Footer";

const NotFound = ({cartProduct}) => {
    return (
        <div style={{height: '100vh', display: 'flex', flexDirection: 'column', justifyContent: 'space-between'}}>
            <Header cartProduct={cartProduct}/>
            <h1 style={{margin: '5.5rem'}}>This page could not be found.</h1>
            <Footer />
        </div>
    )
}

export default NotFound