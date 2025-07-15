import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';

const app = express();
const port = 3000;

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Middleware to parse JSON bodies (needed for fetch JSON POST)
app.use(express.json());
// Middleware to serve static files from 'public' folder
app.use(express.static(path.join(__dirname, '../public')));

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, '../public/index.html'));
});

app.post('/purchase', (req, res) => {
  const { adultTickets, childTickets, infantTickets } = req.body;

  const a = parseInt(adultTickets, 10) || 0;
  const c = parseInt(childTickets, 10) || 0;
  const i = parseInt(infantTickets, 10) || 0;

  const total = a + c + i;

  if (total > 25) {
    return res.json({ error: 'You cannot purchase more than 25 tickets' });
  }

  if (a === 0 && (c > 0 || i > 0)) {
    return res.json({ error: 'At least one Adult ticket is required with Child or Infant tickets' });
  }

  const amount = (a * 25) + (c * 15);
  const seats = a + c;

  return res.json({
    message: 'Purchase successful',
    tickets: { adult: a, child: c, infant: i },
    totalAmount: amount,
    seatsReserved: seats,
  });
});

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
