import React from 'react'
import { useNavigate } from 'react-router'

const NotFound: React.FC = () => {
  const navigate = useNavigate()

  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h1>404 - Page Not Found</h1>
      <p>Sorry, the page you are looking for does not exist.</p>
      <button onClick={() => navigate('/')} style={{ marginTop: '20px', padding: '10px 20px' }}>
        Go to Home
      </button>
    </div>
  )
}

export default NotFound
