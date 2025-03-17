import React from 'react';
import Modal from 'react-modal';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { AiFillCheckCircle , AiFillCloseCircle , AiFillExclamationCircle   } from 'react-icons/ai';

// Modal'ın kök elementini ayarlama
Modal.setAppElement('#root');

const ModalComponent = ({ isOpen, onRequestClose, message, type }) => {
  const getMessageColor = () => {
    if (type === 'success') return 'green';
    if (type === 'error') return 'red';
    if (type === 'warning') return 'orange';
    return 'black'; // Varsayılan renk
  };


  const getIcon = () => {
    if (type === 'success') return <AiFillCheckCircle style={{ color: 'green', fontSize: '40px' }} />;
    if (type === 'error') return <AiFillCloseCircle  style={{ color: 'red', fontSize: '40px' }} />;
    if (type === 'warning') return <AiFillExclamationCircle  style={{ color: 'orange', fontSize: '40px' }} />;
    return null;
  };

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      contentLabel="Bilgilendirme Modalı"
      className="custom-modal"
      overlayClassName="custom-overlay"
    >
      {/* Modal başlık ve ikon */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '5px', marginBottom: '40px' }}>
        {getIcon()}
      </div>

      {/* Mesaj içeriği */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {message.split("\n").map((line, index) => (
          <p key={index} style={{ margin: 0, fontSize: '15px', fontWeight: 'bold', color: getMessageColor() }}>
            {line}
          </p>
        ))}
      </div>

      {/* Kapat butonu */}
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
          color={type === 'success' ? 'success' : type === 'error' ? 'error' : 'warning'}
          onClick={onRequestClose}
        >
          Kapat
        </Button>
      </Stack>
    </Modal>
  );
};

export default ModalComponent;
