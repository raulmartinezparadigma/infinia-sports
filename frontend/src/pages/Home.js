import React from "react";

// Página principal (Home)
import SearchBar from "../components/SearchBar";
import ProductList from "../components/ProductList";

function Home() {
  // Página de inicio del e-commerce
  return (
    <div>
      <h2>Home Page</h2>
      <SearchBar />
      <ProductList />
    </div>
  );
}

export default Home;
