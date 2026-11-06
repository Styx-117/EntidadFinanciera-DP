(function () {
  var reducido = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  function formato(n) {
    return n.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  document.querySelectorAll('[data-contar]').forEach(function (el) {
    var destino = parseFloat(el.getAttribute('data-contar'));
    if (isNaN(destino) || reducido) { return; }
    var decimales = el.hasAttribute('data-entero') ? 0 : 2;
    var inicio = null;
    function paso(t) {
      if (inicio === null) { inicio = t; }
      var p = Math.min((t - inicio) / 900, 1);
      var suave = 1 - Math.pow(1 - p, 3);
      el.textContent = (destino * suave).toLocaleString('en-US', {
        minimumFractionDigits: decimales,
        maximumFractionDigits: decimales
      });
      if (p < 1) { requestAnimationFrame(paso); }
    }
    requestAnimationFrame(paso);
  });

  document.querySelectorAll('.aviso.ok').forEach(function (aviso) {
    setTimeout(function () { aviso.classList.add('oculto'); }, 6000);
  });

  var ver = document.getElementById('ver-clave');
  if (ver) {
    ver.addEventListener('click', function () {
      var campo = document.getElementById('password');
      var visible = campo.type === 'text';
      campo.type = visible ? 'password' : 'text';
      ver.textContent = visible ? 'Mostrar' : 'Ocultar';
    });
  }

  var dialogo = document.getElementById('dialogo');
  var pendiente = null;
  if (dialogo) {
    var texto = dialogo.querySelector('[data-texto]');
    document.querySelectorAll('form[data-confirmar]').forEach(function (f) {
      f.addEventListener('submit', function (e) {
        e.preventDefault();
        texto.textContent = f.getAttribute('data-confirmar').replace(/\{(\w+)\}/g, function (_, nombre) {
          var campo = f.elements[nombre];
          if (!campo) { return ''; }
          if (nombre === 'monto') {
            var numero = parseFloat(campo.value);
            return isNaN(numero) ? campo.value : formato(numero);
          }
          return campo.value;
        });
        pendiente = f;
        dialogo.showModal();
      });
    });
    dialogo.querySelector('[data-cancelar]').addEventListener('click', function () {
      dialogo.close();
      pendiente = null;
    });
    dialogo.querySelector('[data-aceptar]').addEventListener('click', function (e) {
      e.currentTarget.disabled = true;
      e.currentTarget.classList.add('cargando');
      if (pendiente) { pendiente.submit(); }
    });
  }

  document.addEventListener('submit', function (e) {
    var f = e.target;
    if (f.hasAttribute('data-confirmar')) { return; }
    f.querySelectorAll('button[type=submit]').forEach(function (b) {
      setTimeout(function () {
        b.disabled = true;
        b.classList.add('cargando');
      }, 0);
    });
  });

  var sim = document.getElementById('simulador');
  if (sim) {
    var tasa = parseFloat(sim.getAttribute('data-tasa')) / 100 / 12;
    var monto = document.getElementById('monto');
    var rango = document.getElementById('rango');
    var plazo = document.getElementById('plazo');
    var cuotaEl = document.getElementById('sim-cuota');
    var totalEl = document.getElementById('sim-total');
    var calcular = function () {
      var m = parseFloat(monto.value);
      var n = parseInt(plazo.value, 10);
      if (isNaN(m) || isNaN(n) || m <= 0) {
        cuotaEl.textContent = '0.00';
        totalEl.textContent = '0.00';
        return;
      }
      var cuota = tasa === 0 ? m / n : m * tasa / (1 - Math.pow(1 + tasa, -n));
      cuotaEl.textContent = formato(cuota);
      totalEl.textContent = formato(cuota * n);
    };
    monto.addEventListener('input', function () {
      if (monto.value !== '') { rango.value = monto.value; }
      calcular();
    });
    rango.addEventListener('input', function () {
      monto.value = rango.value;
      calcular();
    });
    plazo.addEventListener('change', calcular);
    calcular();
  }
})();
