import React from 'react';
import Modal from 'react-modal';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';

// Modal'ın kök elementini ayarlama
Modal.setAppElement('#root');

const ModalComponent = ({ isOpen, onRequestClose, message, type }) => {
  return (
    <Modal
    isOpen={isOpen}
    onRequestClose={onRequestClose}
    contentLabel="Bilgilendirme Modalı"
    className="custom-modal"
    overlayClassName="custom-overlay"
  >
  <div>
        {/* message'i satırlara ayırıp her birini ayrı <p> etiketiyle göstereceğiz */}
        {message.split("\n").map((line, index) => (
          <p key={index} style={{ margin: '5px 0', fontSize: '14px', color: type === 'success' ? 'green' : 'red' }}>
            {line}
          </p>
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
        color={type === 'success' ? 'success' : 'error'}
        onClick={onRequestClose}
      >
        Kapat
      </Button>
    </Stack>
  </Modal>
  
  );
};

export default ModalComponent;
