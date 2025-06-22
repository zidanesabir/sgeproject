document.addEventListener('DOMContentLoaded', function() {
    console.log("SGE Project - Script loaded!");

    // Example: Simple alert dismissal
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        const dismissButton = document.createElement('button');
        dismissButton.innerHTML = '&times;'; // 'times' entity for 'x'
        dismissButton.classList.add('alert-dismiss-btn');
        dismissButton.onclick = function() {
            alert.style.display = 'none';
        };
        alert.appendChild(dismissButton);
    });

    // Add CSS for the dismiss button (can also be in main.css)
    const style = document.createElement('style');
    style.innerHTML = `
        .alert {
            position: relative;
        }
        .alert-dismiss-btn {
            position: absolute;
            top: 10px;
            right: 15px;
            background: none;
            border: none;
            font-size: 1.5rem;
            line-height: 1;
            cursor: pointer;
            color: inherit;
            opacity: 0.7;
            transition: opacity 0.3s ease;
        }
        .alert-dismiss-btn:hover {
            opacity: 1;
        }
    `;
    document.head.appendChild(style);


    // Example: Smooth scrolling for anchor links (if you have them)
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

    // You would add more specific JavaScript logic here for:
    // - Form validation (e.g., login, registration)
    // - Dynamic content updates (e.g., fetching data with AJAX/Fetch API)
    // - Interactive UI elements (e.g., accordions, tabs, modals)
    // - Data visualization (e.g., charts for statistics.html)
    // - Any client-side logic for the specific Thymeleaf pages.
});

// Function to show a dynamic message (e.g., after an AJAX call)
function showMessage(message, type = 'success') {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('alert', `alert-${type}`);
    messageContainer.textContent = message;

    // Append to a common message area, or at the top of the body/container
    const container = document.querySelector('.container') || document.body;
    if (container) {
        container.insertBefore(messageContainer, container.firstChild);
        // Automatically dismiss after some time
        setTimeout(() => {
            messageContainer.remove();
        }, 5000); // 5 seconds
    }
}

// Example usage:
// showMessage('Data saved successfully!', 'success');
// showMessage('Error fetching data.', 'danger');