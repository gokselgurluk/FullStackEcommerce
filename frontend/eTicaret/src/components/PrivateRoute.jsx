import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // AuthContext'ten isAuth değerini almak için hook

const PrivateRoute = ({ children }) => {
  const { isAuth } = useAuth(); // Kullanıcının kimlik doğrulama durumunu kontrol et

  if (!isAuth) {
    return <Navigate to="/login" />; // Eğer kullanıcı doğrulanmamışsa, login sayfasına yönlendir
  }

  return children; // Eğer kullanıcı doğrulanmışsa, korunan bileşeni göster
};

export default PrivateRoute;
