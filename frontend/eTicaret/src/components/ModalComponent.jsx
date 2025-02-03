import React from 'react';
import Modal from 'react-modal';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { AiOutlineCheckCircle, AiOutlineCloseCircle } from 'react-icons/ai';
// Modal'ın kök elementini ayarlama
Modal.setAppElement('#root');

const ModalComponent = ({ isOpen, onRequestClose, message, type }) => {
  const getMessageColor = () => {
    if (type === 'success') return 'green';
    if (type === 'error') return 'red';
    return 'orange'; // enter türü için renk (isteğe bağlı)
  };

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      contentLabel="Bilgilendirme Modalı"
      className="custom-modal"
      overlayClassName="custom-overlay"
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {message.split("\n").map((line, index) => (
          <div
            key={index}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px', // İkon ve metin arasındaki boşluk
            }}
          >
            {/* İkon */}
            {type === 'success' ? (
              <AiOutlineCheckCircle style={{ color: 'green', fontSize: '50px' }} />
            ) : type === 'error' ? (
              <AiOutlineCloseCircle style={{ color: 'red', fontSize: '50px' }} />
            ) : (
              <AiOutlineCheckCircle style={{ color: 'orange', fontSize: '50px' }} /> // enter için farklı ikon
            )}

            {/* Mesaj */}
            <p
              style={{
                margin: 0,
                fontSize: '20px',
                fontWeight: 'bold', // Font kalınlığı
                color: getMessageColor(), // Renkler dinamik olarak belirleniyor
              }}
            >
              {line}
            </p>
          </div>
        ))}
      </div>
      <Stack
        direction="row"
        spacing={2}
        style={{
          marginTop: '40px',
          justifyContent: 'center',
        }}
      >
        <Button
          variant={type === 'success' ? 'contained' : 'outlined'}
          color={type === 'success' ? 'success' : type === 'error' ? 'error' : 'warning'} // enter türü için 'warning' rengi
          onClick={onRequestClose}
        >
          Kapat
        </Button>
      </Stack>
    </Modal>
  );
};

export default ModalComponent;
