import React from "react";

const HomePage = () => {
  return (
    <div style={{ textAlign: "center", padding: "20px" }}>
      <h1>Welcome to the Home Page</h1>
      <p>This is the main landing page of your website.</p>
      <img
        src="https://via.placeholder.com/600x300"
        alt="Placeholder"
        style={{ maxWidth: "100%", height: "auto", margin: "20px 0" }}
      />
      <button
        style={{
          padding: "10px 20px",
          fontSize: "16px",
          backgroundColor: "#007bff",
          color: "#fff",
          border: "none",
          borderRadius: "5px",
          cursor: "pointer",
        }}
        onClick={() => alert("Welcome!")}
      >
        Learn More
      </button>
    </div>
  );
};

export default HomePage;
